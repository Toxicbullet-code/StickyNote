import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class note {
    private int mouseX;
    private int mouseY;
    boolean isDarkMode = false;
    private static final Path session = Path.of("/tmp/javanote_session.txt");

    note() {
        // Make the frame
        JFrame note = new JFrame();
        note.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        note.setUndecorated(true);
        note.setSize(300, 300);

        // The area where you are able to write
        JTextArea text = new JTextArea();
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setMargin(new Insets(8, 8, 8, 8));

        if (Files.exists(session)) {
            try {
                text.setText(Files.readString(session));
            } catch (IOException e) {
                System.err.println("could not load session: " + e.getMessage());
            }
        }

        text.getDocument().addDocumentListener(new DocumentListener() {
            private void saveSession() {
                try {
                    Files.writeString(session, text.getText());
                } catch (IOException e) {
                    System.err.println("could not save to session: " + e.getMessage());
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) { saveSession(); }

            @Override
            public void removeUpdate(DocumentEvent e) { saveSession(); }

            @Override
            public void changedUpdate(DocumentEvent e) { saveSession(); }
        });

        // Able to scroll and helps with managing text
        JScrollPane scroll = new JScrollPane(text);
        scroll.setBorder(null);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Makes the note moveable
        MouseAdapter drag = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int newX = e.getXOnScreen() - mouseX;
                int newY = e.getYOnScreen() - mouseY;
                note.setLocation(newX, newY);
            }
        };

        // Sets panel that has the buttons on and makes it draggable
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bar.addMouseListener(drag);
        bar.addMouseMotionListener(drag);

        // Sets close button
        JButton closeButton = new JButton("X");
        closeButton.setPreferredSize(new Dimension(45, 25));
        closeButton.addActionListener(e -> System.exit(0));

        // Sets the darkmode button and its properties
        JButton darkMode = new JButton("d");
        darkMode.setPreferredSize(new Dimension(40, 25));
        darkMode.addActionListener(e -> {
            isDarkMode = !isDarkMode;
            if (isDarkMode) {
                text.setBackground(Color.BLACK);
                text.setForeground(Color.white);
                text.setCaretColor(Color.white);
                bar.setBackground(Color.black);
                darkMode.setBackground(Color.black);
                darkMode.setForeground(Color.white);
                closeButton.setBackground(Color.black);
                closeButton.setForeground(Color.white);
            } else {
                text.setBackground(Color.white);
                text.setForeground(Color.black);
                text.setCaretColor(Color.black);
                bar.setBackground(Color.white);
                darkMode.setBackground(Color.white);
                darkMode.setForeground(Color.BLACK);
                closeButton.setBackground(Color.white);
                closeButton.setForeground(Color.black);
            }
        });

        // Adds the buttons to the panel on the top
        bar.add(darkMode);
        bar.add(closeButton);

        // Make the buttons smoother
        darkMode.setBorderPainted(false);
        closeButton.setBorderPainted(false);
        darkMode.setFocusable(false);
        closeButton.setFocusable(false);

        // Assemble
        note.add(bar, BorderLayout.NORTH);
        note.add(scroll, BorderLayout.CENTER);
        note.setAlwaysOnTop(true);
        note.setVisible(true);
    }
}