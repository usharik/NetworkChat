package ru.geekbrains.client.swing;


import ru.geekbrains.client.AuthException;
import ru.geekbrains.client.Network;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RegDialog extends JDialog {

    private Network network;
    private JTextField tfUsername;
    private JPasswordField pfPassword1;
    private JPasswordField pfPassword2;
    private JLabel lbUsername;
    private JLabel lbPassword1;
    private JLabel lbPassword2;
    private JButton btnReg;
    private JButton btnCancel;

    public RegDialog(JDialog parent, Network network){
        super(parent, "Регистрация", true);
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

        lbPassword1 = new JLabel("Пароль: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword1, cs);

        lbPassword2 = new JLabel("Подтверждение пароля: ");
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(lbPassword2, cs);

        pfPassword1 = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword1, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        pfPassword2 = new JPasswordField(20);
        cs.gridx = 2;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(pfPassword2, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        btnReg = new JButton("Регистрация");
        btnCancel = new JButton("Отмена");

        JPanel bp = new JPanel();

        bp.add(btnReg);
        btnReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tfUsername.getText().isEmpty() || String.valueOf(pfPassword1.getPassword()).isEmpty()
                        || String.valueOf(pfPassword2.getPassword()).isEmpty()){
                    JOptionPane.showMessageDialog(RegDialog.this,
                            "Введите имя пользователя или пароль",
                            "Пустые поля не допустимы",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }else if(!String.valueOf(pfPassword1.getPassword()).equals(String.valueOf(pfPassword2.getPassword()))){
                    JOptionPane.showMessageDialog(RegDialog.this,
                            "Пароли не совпадают",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }else {
                    try {
                        network.registration(tfUsername.getText(), String.valueOf(pfPassword1.getPassword()));
                        JOptionPane.showMessageDialog(RegDialog.this,
                                "Регистрация пользователя " + tfUsername.getText() + " прошла успешно",
                                "Регистрация",
                                JOptionPane.INFORMATION_MESSAGE);

                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(RegDialog.this,
                                "Ошибка сети",
                                "Регистрация",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    } catch (AuthException ex) {
                        JOptionPane.showMessageDialog(RegDialog.this,
                                "Ошибка",
                                "Регистрация",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
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
