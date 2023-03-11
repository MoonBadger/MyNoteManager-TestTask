import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainForm extends JFrame {
    private JPanel panelMain;
    private JButton okButton;
    private JButton addButton;
    private JButton cancerButton;
    private JButton deleteButton;
    private JComboBox<String> comboBox;
    private JTextField titleTextField;
    private JTextArea mainTextArea;
    private JLabel timeLabel;

    private final ConfirmationForm confirmationForm = new ConfirmationForm(this);

    private static final String JSON_PATH = "data/retention.json",
            DATE_FORMAT = "dd.MM.yyyy hh:mm",
            DEFAULT_TITLE = "<Без названия>",
            DEFAULT_ADD_TEXT= "Добавление новой заметки",
            DEFAULT_ADD_TITLE = "Заголовок заметки",
            DEFAULT_ADD_MESSAGE = "Создание новой заметки",
            TITLES_NOT_EXISTING_TEXT = "Пока ещё нет ни одной заметки";

    private enum Mode {
        READ,
        EDIT,
        ADD
    }
    private Mode mode = Mode.READ;
    private final NoteManager manager = NoteManager.getInstance();

    private boolean confirm = false;

    public MainForm() {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(this.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        mainTextArea.setWrapStyleWord(true);
        blockRedactButtons();

        manager.load(JSON_PATH);

        if(manager.getNotes().isEmpty()) {
            Note testNote = new Note("Заголовок", "текст");
            manager.setCurrentNoteId(0);
            manager.getNotes().add(testNote);
            manager.save(JSON_PATH);
        }
        update();

        mainTextArea.getDocument().addDocumentListener(new ChangeListener());
        titleTextField.getDocument().addDocumentListener(new ChangeListener());

        okButton.addActionListener(e -> {
            if(mode == Mode.EDIT || mode == Mode.READ) {
                manager.getCurrentNote().setTitle(titleTextField.getText());
                manager.getCurrentNote().setContent(mainTextArea.getText());
                manager.save(JSON_PATH);
                showNote();
            } else if(mode == Mode.ADD) {
                String title = titleTextField.getText();
                String content = mainTextArea.getText();
                if(title.equals("")) {
                    title = DEFAULT_TITLE;
                }
                Note note = new Note(title, content);
                manager.getNotes().add(note);
                addButton.setEnabled(true);
                comboBox.setEnabled(true);
                manager.save(JSON_PATH);
            }
            blockRedactButtons();
            updateComboBox();
            manager.setCurrentNoteId(manager.getNotes().size() - 1);
            comboBox.setSelectedIndex(manager.getCurrentNoteId());
            showNote();
            mode = Mode.READ;
            blockRedactButtons();
        });

        addButton.addActionListener(e -> {
            deleteButton.setEnabled(true);
            manager.load(JSON_PATH);
            mode = Mode.ADD;
            addButton.setEnabled(false);
            okButton.setEnabled(true);
            comboBox.setEnabled(false);
            titleTextField.setText(DEFAULT_ADD_TITLE);
            mainTextArea.setText(DEFAULT_ADD_TEXT);
            timeLabel.setText(DEFAULT_ADD_MESSAGE);
            cancerButton.setEnabled(false);
        });

        cancerButton.addActionListener(e -> {
            manager.load(JSON_PATH);
            update();
            if(! (mode == Mode.ADD)) {
                okButton.setEnabled(false);
            }
            cancerButton.setEnabled(false);
        });

        comboBox.addItemListener(e -> {
            int ind = comboBox.getSelectedIndex();
            if(ind >= 0) {
                manager.setCurrentNoteId(ind);
                showNote();
                blockRedactButtons();
            }
        });

        deleteButton.addActionListener(e -> {
            confirmationForm.setVisible(true);
            if(! confirm) return;
            if(mode == Mode.ADD) {
                addButton.setEnabled(true);
                comboBox.setEnabled(true);
                manager.load(JSON_PATH);
                showNote();
                okButton.setEnabled(false);
                mode = Mode.READ;
            } else {
                if(! manager.getNotes().isEmpty()) {
                    if(confirm) {
                        manager.setCurrentNoteId(comboBox.getSelectedIndex());
                        manager.deleteCurrentNote();
                        manager.save(JSON_PATH);
                        manager.setCurrentNoteId(0);
                        update();
                    }
                }
                confirm = false;
            }
            if(manager.getNotes().isEmpty()) {
                deleteButton.setEnabled(false);
                mainTextArea.setText(TITLES_NOT_EXISTING_TEXT);
                titleTextField.setText("");
                updateComboBox();
            }
            Note cn = manager.getCurrentNote();
            if(cn == null || (! mainTextArea.getText().equals(cn.getContent()) &&
                    ! titleTextField.getText().equals(cn.getTitle())
                    )) {
                blockRedactButtons();
            }
        });
    }

    private void showNote() {
        try {
            Note note = manager.getCurrentNote();
            timeLabel.setText(dateToString(note.getDatetime()));
            mainTextArea.setText(note.getContent());
            titleTextField.setText(note.getTitle());
        } catch (Exception ignored) {}
    }

    private String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(date);
    }

    private void updateComboBox() {
        comboBox.removeAllItems();
        List<Note> list = manager.getNotes();
        String[] items = new String[list.size()];
        for (int i = 0; i < items.length; i++) {
            Note note = list.get(i);
            items[i] = note.getTitle() +  " ".repeat(3) + "(" + dateToString(note.getDatetime()) + ")";
            comboBox.addItem(items[i]);
        }
        if(! manager.getNotes().isEmpty()) {
            comboBox.setSelectedIndex(manager.getCurrentNoteId());
        }
    }

    private void update() {
        showNote();
        updateComboBox();
    }

    private void blockRedactButtons() {
        okButton.setEnabled(false);
        cancerButton.setEnabled(false);
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    private class ChangeListener implements DocumentListener {
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
            if(deleteButton.isEnabled()) {
                if (mode != Mode.ADD) {
                    cancerButton.setEnabled(true);
                }
                okButton.setEnabled(true);
            }
            if(mode == Mode.READ) {
                mode = Mode.EDIT;
            }
        }
    }
}
