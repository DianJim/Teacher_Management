package com.example.teachermanagement.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("外聘教师管理系统");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        JButton courseButton = new JButton("课程管理");
        JButton teacherButton = new JButton("教师管理");
        JButton scheduleButton = new JButton("教学安排管理");

        courseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CourseManagement().setVisible(true);
            }
        });

        teacherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TeacherManagement().setVisible(true);
            }
        });

        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ScheduleManagement().setVisible(true);
            }
        });

        add(courseButton);
        add(teacherButton);
        add(scheduleButton);
    }
}
