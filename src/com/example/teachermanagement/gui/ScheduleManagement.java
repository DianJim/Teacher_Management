package com.example.teachermanagement.gui;

import com.example.teachermanagement.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ScheduleManagement extends JFrame {
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;  // 查找框
    private JComboBox<String> searchByComboBox;  // 查找条件选择框

    public ScheduleManagement() {
        setTitle("教学安排管理");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建表格模型，不再包含 ID 列
        tableModel = new DefaultTableModel(new String[]{"教师姓名", "课程名称"}, 0);
        scheduleTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        add(scrollPane, BorderLayout.CENTER);

        // 底部面板：添加按钮、删除按钮、修改按钮和查找按钮
        JPanel bottomPanel = new JPanel();
        JButton addButton = new JButton("添加安排");
        JButton deleteButton = new JButton("删除安排");
        JButton updateButton = new JButton("修改安排");
        JButton searchButton = new JButton("查找");

        searchField = new JTextField(20);  // 查找框
        String[] searchOptions = {"教师姓名", "课程名称"};  // 查找条件选择
        searchByComboBox = new JComboBox<>(searchOptions);  // 查找条件选择框

        bottomPanel.add(searchByComboBox);
        bottomPanel.add(searchField);
        bottomPanel.add(searchButton);
        bottomPanel.add(addButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(updateButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // 查找按钮的事件处理
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText().trim();
                String searchBy = (String) searchByComboBox.getSelectedItem();
                if (!searchText.isEmpty()) {
                    searchSchedule(searchBy, searchText);  // 根据选择的条件进行查找
                } else {
                    loadSchedules();  // 若没有输入搜索内容，则加载所有安排
                }
            }
        });

        // 添加教学安排的按钮事件处理
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String teacherName = JOptionPane.showInputDialog("请输入教师姓名：");
                String courseName = JOptionPane.showInputDialog("请输入课程名称：");
                if (teacherName != null && !teacherName.trim().isEmpty() && courseName != null && !courseName.trim().isEmpty()) {
                    addSchedule(teacherName, courseName);  // 添加教学安排
                }
            }
        });

        // 删除教学安排的按钮事件处理
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = scheduleTable.getSelectedRow();
                if (selectedRow != -1) {
                    String teacherName = (String) scheduleTable.getValueAt(selectedRow, 0);
                    String courseName = (String) scheduleTable.getValueAt(selectedRow, 1);
                    deleteSchedule(teacherName, courseName);  // 删除教学安排
                } else {
                    JOptionPane.showMessageDialog(null, "请选择要删除的教学安排！");
                }
            }
        });

        // 修改教学安排的按钮事件处理
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = scheduleTable.getSelectedRow();
                if (selectedRow != -1) {
                    String teacherName = (String) scheduleTable.getValueAt(selectedRow, 0);
                    String courseName = (String) scheduleTable.getValueAt(selectedRow, 1);
                    String newTeacherName = JOptionPane.showInputDialog("请输入新的教师姓名：", teacherName);
                    String newCourseName = JOptionPane.showInputDialog("请输入新的课程名称：", courseName);
                    if (newTeacherName != null && !newTeacherName.trim().isEmpty() && newCourseName != null && !newCourseName.trim().isEmpty()) {
                        updateSchedule(teacherName, courseName, newTeacherName, newCourseName);  // 修改教学安排
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请选择要修改的教学安排！");
                }
            }
        });

        loadSchedules();  // 初始化加载教学安排
    }//ui

    // 加载所有教学安排
    private void loadSchedules() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT t.name AS teacher_name, c.name AS course_name FROM teaching_schedules ts " +
                    "JOIN teachers t ON ts.teacher_id = t.id " +
                    "JOIN courses c ON ts.course_id = c.id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);  // 清空表格数据
            while (rs.next()) {
                String teacherName = rs.getString("teacher_name");
                String courseName = rs.getString("course_name");
                tableModel.addRow(new Object[]{teacherName, courseName});  // 添加到表格中
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 根据教师姓名或课程名称查找教学安排
    private void searchSchedule(String searchBy, String searchText) {
        String sql = "";
        if ("教师姓名".equals(searchBy)) {
            sql = "SELECT t.name AS teacher_name, c.name AS course_name FROM teaching_schedules ts " +
                    "JOIN teachers t ON ts.teacher_id = t.id " +
                    "JOIN courses c ON ts.course_id = c.id " +
                    "WHERE t.name LIKE ?";
        } else if ("课程名称".equals(searchBy)) {
            sql = "SELECT t.name AS teacher_name, c.name AS course_name FROM teaching_schedules ts " +
                    "JOIN teachers t ON ts.teacher_id = t.id " +
                    "JOIN courses c ON ts.course_id = c.id " +
                    "WHERE c.name LIKE ?";
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchText + "%");  // 使用模糊匹配
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);  // 清空表格数据
            while (rs.next()) {
                String teacherName = rs.getString("teacher_name");
                String courseName = rs.getString("course_name");
                tableModel.addRow(new Object[]{teacherName, courseName});  // 添加到表格中
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 添加教学安排
    private void addSchedule(String teacherName, String courseName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 获取教师 ID
            String teacherQuery = "SELECT id FROM teachers WHERE name = ?";
            PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery);
            teacherStmt.setString(1, teacherName);
            ResultSet teacherResult = teacherStmt.executeQuery();

            int teacherId = -1;
            if (teacherResult.next()) {
                teacherId = teacherResult.getInt("id");
            } else {
                JOptionPane.showMessageDialog(null, "教师不存在！");
                return;
            }

            // 获取课程 ID
            String courseQuery = "SELECT id FROM courses WHERE name = ?";
            PreparedStatement courseStmt = conn.prepareStatement(courseQuery);
            courseStmt.setString(1, courseName);
            ResultSet courseResult = courseStmt.executeQuery();

            int courseId = -1;
            if (courseResult.next()) {
                courseId = courseResult.getInt("id");
            } else {
                JOptionPane.showMessageDialog(null, "课程不存在！");
                return;
            }

            // 插入教学安排
            String insertQuery = "INSERT INTO teaching_schedules (teacher_id, course_id) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, teacherId);
            insertStmt.setInt(2, courseId);
            insertStmt.executeUpdate();
            loadSchedules();  // 刷新列表
            JOptionPane.showMessageDialog(null, "教学安排添加成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 删除教学安排
    private void deleteSchedule(String teacherName, String courseName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 获取教师 ID 和课程 ID
            String teacherQuery = "SELECT id FROM teachers WHERE name = ?";
            PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery);
            teacherStmt.setString(1, teacherName);
            ResultSet teacherResult = teacherStmt.executeQuery();

            int teacherId = -1;
            if (teacherResult.next()) {
                teacherId = teacherResult.getInt("id");
            } else {
                JOptionPane.showMessageDialog(null, "教师不存在！");
                return;
            }

            String courseQuery = "SELECT id FROM courses WHERE name = ?";
            PreparedStatement courseStmt = conn.prepareStatement(courseQuery);
            courseStmt.setString(1, courseName);
            ResultSet courseResult = courseStmt.executeQuery();

            int courseId = -1;
            if (courseResult.next()) {
                courseId = courseResult.getInt("id");
            } else {
                JOptionPane.showMessageDialog(null, "课程不存在！");
                return;
            }

            // 删除教学安排
            String deleteQuery = "DELETE FROM teaching_schedules WHERE teacher_id = ? AND course_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, teacherId);
            deleteStmt.setInt(2, courseId);
            deleteStmt.executeUpdate();
            loadSchedules();  // 刷新列表
            JOptionPane.showMessageDialog(null, "教学安排删除成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 修改教学安排
    private void updateSchedule(String teacherName, String courseName, String newTeacherName, String newCourseName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 获取新的教师 ID
            String teacherQuery = "SELECT id FROM teachers WHERE name = ?";
            PreparedStatement teacherStmt = conn.prepareStatement(teacherQuery);
            teacherStmt.setString(1, newTeacherName);
            ResultSet teacherResult = teacherStmt.executeQuery();

            int teacherId = -1;
            if (teacherResult.next()) {
                teacherId = teacherResult.getInt("id");
            } else {
                JOptionPane.showMessageDialog(null, "教师不存在！");
                return;
            }

            // 获取新的课程 ID
            String courseQuery = "SELECT id FROM courses WHERE name = ?";
            PreparedStatement courseStmt = conn.prepareStatement(courseQuery);
            courseStmt.setString(1, newCourseName);
            ResultSet courseResult = courseStmt.executeQuery();

            int courseId = -1;
            if (courseResult.next()) {
                courseId = courseResult.getInt("id");
            } else {
                JOptionPane.showMessageDialog(null, "课程不存在！");
                return;
            }

            // 更新教学安排
            String updateQuery = "UPDATE teaching_schedules SET teacher_id = ?, course_id = ? WHERE teacher_id = ? AND course_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, teacherId);
            updateStmt.setInt(2, courseId);
            updateStmt.setInt(3, teacherId);
            updateStmt.setInt(4, courseId);
            updateStmt.executeUpdate();
            loadSchedules();  // 刷新列表
            JOptionPane.showMessageDialog(null, "教学安排更新成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }
}
