package com.example.teachermanagement.gui;

import com.example.teachermanagement.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TeacherManagement extends JFrame {
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public TeacherManagement() {
        setTitle("教师管理");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建表格模型，包含教师姓名和电话号码
        tableModel = new DefaultTableModel(new String[]{"教师姓名", "电话号码"}, 0);
        teacherTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        add(scrollPane, BorderLayout.CENTER);

        // 底部面板：添加按钮、删除按钮、修改按钮和查找按钮
        JPanel bottomPanel = new JPanel();
        JButton addButton = new JButton("添加教师");
        JButton deleteButton = new JButton("删除教师");
        JButton updateButton = new JButton("修改教师");
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
                    searchTeacher(searchText);
                } else {
                    loadTeachers();  // 若没有输入搜索内容，则加载所有教师
                }
            }
        });

        // 添加教师的按钮事件处理
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String teacherName = JOptionPane.showInputDialog("请输入教师姓名：");
                if (teacherName != null && !teacherName.trim().isEmpty()) {
                    String phoneNumber = JOptionPane.showInputDialog("请输入教师电话号码：");
                    if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                        addTeacher(teacherName, phoneNumber);  // 添加教师
                    }
                }
            }
        });

        // 删除教师的按钮事件处理
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = teacherTable.getSelectedRow();
                if (selectedRow != -1) {
                    String teacherName = (String) teacherTable.getValueAt(selectedRow, 0);
                    deleteTeacher(teacherName);  // 删除教师
                } else {
                    JOptionPane.showMessageDialog(null, "请选择要删除的教师！");
                }
            }
        });

        // 修改教师信息的按钮事件处理
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = teacherTable.getSelectedRow();
                if (selectedRow != -1) {
                    String oldTeacherName = (String) teacherTable.getValueAt(selectedRow, 0);
                    String newTeacherName = JOptionPane.showInputDialog("请输入新的教师姓名：");
                    String newPhoneNumber = JOptionPane.showInputDialog("请输入新的电话号码：");
                    if (newTeacherName != null && !newTeacherName.trim().isEmpty() && newPhoneNumber != null && !newPhoneNumber.trim().isEmpty()) {
                        updateTeacher(oldTeacherName, newTeacherName, newPhoneNumber);  // 修改教师信息
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请选择要修改的教师！");
                }
            }
        });

        loadTeachers();  // 初始化加载教师列表
    }

    // 加载所有教师
    private void loadTeachers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM teachers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                String name = rs.getString("name");
                String phoneNumber = rs.getString("phone_number");
                tableModel.addRow(new Object[]{name, phoneNumber});  // 添加到表格中
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 根据教师姓名查找教师
    private void searchTeacher(String searchText) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM teachers WHERE name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchText + "%");  // 使用模糊匹配
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                String name = rs.getString("name");
                String phoneNumber = rs.getString("phone_number");
                tableModel.addRow(new Object[]{name, phoneNumber});  // 添加到表格中
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 添加教师
    private void addTeacher(String name, String phoneNumber) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO teachers (name, phone_number) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, phoneNumber);
            stmt.executeUpdate();  // 执行插入
            loadTeachers();  // 刷新列表
            JOptionPane.showMessageDialog(null, "教师添加成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 删除教师
    private void deleteTeacher(String name) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM teachers WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.executeUpdate();  // 执行删除
            loadTeachers();  // 刷新列表
            JOptionPane.showMessageDialog(null, "教师删除成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }

    // 修改教师信息
    private void updateTeacher(String oldName, String newName, String newPhoneNumber) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE teachers SET name = ?, phone_number = ? WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.setString(2, newPhoneNumber);
            stmt.setString(3, oldName);
            stmt.executeUpdate();  // 执行更新
            loadTeachers();  // 刷新列表
            JOptionPane.showMessageDialog(null, "教师更新成功！");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误：" + ex.getMessage());
        }
    }
}
