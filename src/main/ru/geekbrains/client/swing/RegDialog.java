package ru.geekbrains.client.swing;

import ru.geekbrains.client.AuthException;
import ru.geekbrains.client.Network;
import ru.geekbrains.client.RegException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RegDialog extends JDialog {
    private Network network;
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JPasswordField pfPassword2;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JLabel lbPassword2;
    private JButton btnReg;
    private JButton btnCancel;


    public RegDialog(JDialog parent, Network network) {
        super(parent, "Логин", true);
        this.network = network;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        lbUsername = new JLabel("Имя пользователя: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        lbPassword = new JLabel("Пароль: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        lbPassword2 = new JLabel("Повторите Пароль: ");
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        panel.add(lbPassword2, cs);

        pfPassword2 = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        panel.add(pfPassword2, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        btnReg = new JButton("Зарегировать");
        btnCancel = new JButton("Отмена");

        JPanel bp = new JPanel();

        bp.add(btnReg);
        btnReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (String.valueOf(pfPassword.getPassword()).equals(String.valueOf(pfPassword2.getPassword()))) {
                        network.regNewUser(tfUsername.getText(), String.valueOf(pfPassword.getPassword()));
                    } else {
                        JOptionPane.showMessageDialog(RegDialog.this,
                                "Пароль не совпадает",
                                "Регистрация",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(RegDialog.this,
                            "Ошибка сети",
                            "Регистрация",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (RegException ex) {
                    JOptionPane.showMessageDialog(RegDialog.this,
                            "Ошибка регистрации",
                            "Регистрация",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dispose();
            }
        });

        bp.add(btnCancel);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }
}
