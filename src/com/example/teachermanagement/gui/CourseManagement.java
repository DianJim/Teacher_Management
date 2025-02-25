package com.example.teachermanagement.gui;

import com.example.teachermanagement.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CourseManagement extends JFrame {
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public CourseManagement() {
        setTitle("课程管理");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建表格模型，包含课程名称
        tableModel = new DefaultTableModel(new String[]{"课程名称"}, 0);
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);

        // 底部面板：添加按钮、删除按钮、修改按钮和查找按钮
        JPanel bottomPanel = new JPanel();
        JButton addButton = new JButton("添加课程");
        JButton deleteButton = new JButton("删除课程");
        JButton updateButton = new JButton("修改课程");
        JButton searchButton = new JButton("查找");
        searchField = new JTextField(20);  // 查找框

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
                if (!searchText.isEmpty()) {
                    searchCourse(searchText);
                } else {
                    loadCourses();  // 若没有输入搜索内容，则加载所有课程
                }
            }
        });

        // 添加课程的按钮事件处理
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String courseName = JOptionPane.showInputDialog("请输入课程名称：");
                if (courseName != null && !courseName.trim().isEmpty()) {
                    addCourse(courseName);  // 添加课程
                }
            }
        });

        // 删除课程的按钮事件处理
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow != -1) {
                    String courseName = (String) courseTable.getValueAt(selectedRow, 0);
                    deleteCourse(courseName);  // 删除课程
                } else {
                    JOptionPane.showMessageDialog(null, "请选择要删除的课程！");
                }
            }
        });

        // 修改课程信息的按钮事件处理
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow != -1) {
                    String oldCourseName = (String) courseTable.getValueAt(selectedRow, 0);
                    String newCourseName = JOptionPane.showInputDialog("请输入新的课程名称：");
                    if (newCourseName != null && !newCourseName.trim().isEmpty()) {
                        updateCourse(oldCourseName, newCourseName);  // 修改课程信息
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请选择要修改的课程！");
                }
            }
        });

        loadCourses();  // 初始化加载课程列表
    }

    // 加载所有课程
    private void loadCourses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM courses";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                String name = rs.getString("name");
                tableModel.addRow(new Object[]{name});  // 添加到表格中
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 根据课程名称查找课程
    private void searchCourse(String searchText) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM courses WHERE name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchText + "%");  // 使用模糊匹配
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                String name = rs.getString("name");
                tableModel.addRow(new Object[]{name});  // 添加到表格中
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 添加课程
    private void addCourse(String name) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO courses (name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.executeUpdate();  // 执行插入
            loadCourses();  // 刷新列表
            JOptionPane.showMessageDialog(null, "课程添加成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 删除课程
    private void deleteCourse(String name) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM courses WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.executeUpdate();  // 执行删除
            loadCourses();  // 刷新列表
            JOptionPane.showMessageDialog(null, "课程删除成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }
    // 修改课程信息
    private void updateCourse(String oldName, String newName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE courses SET name = ? WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            stmt.executeUpdate();  // 执行更新
            loadCourses();  // 刷新列表
            JOptionPane.showMessageDialog(null, "课程更新成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }
}
