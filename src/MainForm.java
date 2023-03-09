import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainForm extends JFrame {
    private static final String JSON_PATH = "data/retention.json";
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

        Note testNote = new Note("Заголовок", "текст");
        manager.setCurrentNoteId(0);
        manager.getNotes().add(testNote);
        showNote(this);


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

    private static void showNote(MainForm mainForm) {
        Note note = NoteManager.getInstance().getCurrentNote();
        Date date = note.getDatetime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        mainForm.timeLabel.setText(" ".repeat(5) + formatter.format(date));
        mainForm.mainTextArea.setText(note.getContent());
        mainForm.titleTextField.setText(note.getTitle());
    }
}
