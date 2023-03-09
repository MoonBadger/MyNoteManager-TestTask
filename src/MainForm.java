import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class MainForm extends JFrame {
    private static final String JSON_PATH = "data/retention.json";
    private static final String DATE_FORMAT = "dd.MM.yyyy hh:mm";
    private JPanel panelMain;
    private JButton okButton;
    private JButton addButton;
    private JButton cancerButton;
    private JButton deleteButton;
    private JComboBox comboBox;
    private JTextField titleTextField;
    private JTextArea mainTextArea;
    private JLabel timeLabel;

    private enum Mode {
        READ,
        EDIT,
        ADD
    }
    private Mode mode = Mode.READ;

    private NoteManager manager = NoteManager.getInstance();

    public MainForm() {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(this.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        mainTextArea.setWrapStyleWord(true);
        okButton.setEnabled(false);

        manager.load(JSON_PATH);

        if(manager.getNotes().isEmpty()) {
            Note testNote = new Note("Заголовок", "текст");
            manager.setCurrentNoteId(0);
            manager.getNotes().add(testNote);
        }
        showNote();
        updateComboBox();

        mainTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                change();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}

            private void change() {
                if(mode == Mode.READ) {
                    mode = Mode.EDIT;
                    okButton.setEnabled(true);
                } else {
                    manager.getCurrentNote().setContent(mainTextArea.getText());
                }
            }
        });

        okButton.addActionListener(e -> {
            if(mode == Mode.EDIT) {
                mode = Mode.READ;
                manager.save(JSON_PATH);
                okButton.setEnabled(false);
            }
        });
    }

    private void showNote() {
        Note note = NoteManager.getInstance().getCurrentNote();
        Date date = note.getDatetime();
        timeLabel.setText(" ".repeat(5) + dateToString(date));
        mainTextArea.setText(note.getContent());
        titleTextField.setText(note.getTitle());
    }

    private String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(date);
    }

    private void updateComboBox() {
        List<Note> list = manager.getNotes();
        String[] items = new String[list.size()];
        for (int i = 0; i < items.length; i++) {
            Note note = list.get(i);
            items[i] = note.getTitle() +
                    " ".repeat(3) + "(" +dateToString(note.getDatetime()) + ")";
            comboBox.addItem(items[i]);
        }

    }
}
