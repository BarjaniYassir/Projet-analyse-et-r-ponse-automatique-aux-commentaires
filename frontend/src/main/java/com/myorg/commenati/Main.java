package com.myorg.commentai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Main {
    private static final String API_BASE = "http://127.0.0.1:5000"; // backend must run here
    private static final String DB_FILE = "comments.db";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Comment-AI - Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 500);
            frame.setLayout(new BorderLayout());

            JTextArea inputArea = new JTextArea(5, 50);
            JScrollPane inputScroll = new JScrollPane(inputArea);

            JButton sendBtn = new JButton("Envoyer");
            JButton historyBtn = new JButton("Voir historique");

            JTextArea outputArea = new JTextArea(10, 50);
            outputArea.setEditable(false);
            JScrollPane outputScroll = new JScrollPane(outputArea);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(new JLabel("Entrez un commentaire :"), BorderLayout.NORTH);
            topPanel.add(inputScroll, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel();
            btnPanel.add(sendBtn);
            btnPanel.add(historyBtn);
            topPanel.add(btnPanel, BorderLayout.SOUTH);

            frame.add(topPanel, BorderLayout.NORTH);
            frame.add(outputScroll, BorderLayout.CENTER);

            // Initialize API client and DB helper
            ApiClient apiClient = new ApiClient(API_BASE);
            DatabaseHelper db = new DatabaseHelper(DB_FILE);

            sendBtn.addActionListener((ActionEvent e) -> {
                String text = inputArea.getText().trim();
                if (text.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Veuillez écrire un commentaire.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                outputArea.setText("Analyse en cours...");
                // run in background thread to avoid freezing UI
                new Thread(() -> {
                    try {
                        ApiClient.AnalysisResult res = apiClient.analyzeText(text);
                        // Save to DB
                        db.insertRecord(text, res.sentiment, res.response);
                        // Update UI (on EDT)
                        SwingUtilities.invokeLater(() -> {
                            outputArea.setText("Commentaire : " + text + "\n\n" +
                                    "Sentiment détecté : " + res.sentiment + "\n\n" +
                                    "Réponse générée : " + res.response);
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        SwingUtilities.invokeLater(() -> {
                            outputArea.setText("Erreur lors de l'appel à l'API : " + ex.getMessage());
                        });
                    }
                }).start();
            });

            historyBtn.addActionListener((ActionEvent e) -> {
                List<String> rows = db.getAllRecords();
                JTextArea histArea = new JTextArea(20, 50);
                for (String r : rows) histArea.append(r + "\n\n");
                histArea.setEditable(false);
                JScrollPane scroll = new JScrollPane(histArea);
                JOptionPane.showMessageDialog(frame, scroll, "Historique des commentaires", JOptionPane.PLAIN_MESSAGE);
            });

            frame.setVisible(true);
        });
    }
}
