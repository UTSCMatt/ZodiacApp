package gui;


import zodiac.action.QuestionAction;
import zodiac.action.StudentAction;
import zodiac.definition.Student;
import zodiac.definition.coursework.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

public class AssignmentUI extends JFrame implements  ActionListener{

	private JPanel contentPane;
	private Assignment assignment;
	private ArrayList<Question> questions;
	private Student student;
	private AssignmentUI ref = this;
	private  int currentAt = 0;
	ArrayList<Integer> answers;
	ArrayList<JRadioButton> rdbts;
	JButton btnSaveNext;
	JButton btnCancle;
	JButton btnPrev;
	JLabel lblNewLabel;
	ButtonGroup group;
	JScrollPane scrollPane;
	public AssignmentUI(Assignment ass,Student stud) {
	    // -1 id marks a new assignment not in the database
		super("Assignment " + ass.getId());
	    this.student = stud;
		this.assignment = ass;
	    this.questions = (ArrayList<Question>)new QuestionAction().getQuestionsWithAnswer(ass.getId());
	    this.createContents();



	 }


	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 350);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);


		//  buttons
		btnSaveNext = new JButton();
		btnSaveNext.setBounds(238, 291, 94, 28);
		btnSaveNext.setText("Save & Next");
		btnSaveNext.setEnabled(false);
		contentPane.add(btnSaveNext);
									
		btnCancle = new JButton();
		btnCancle.setBounds(346, 291, 94, 28);
		btnCancle.setText("Cancel");
		contentPane.add(btnCancle);
									
		btnPrev = new JButton();
		btnPrev.setBounds(130, 291, 94, 28);
		btnPrev.setText("Back");
		btnPrev.setEnabled(false);
		contentPane.add(btnPrev);
		// question label				
		lblNewLabel = new JLabel("1. "+this.questions.get(0).getQuestion());
		lblNewLabel.setBounds(40, 34, 350, 40);
		contentPane.add(lblNewLabel);
		// setting answer options
		group = new ButtonGroup();


		answers = new ArrayList<Integer>();
		rdbts = new ArrayList<JRadioButton>();
		int i;
		for (i=0;i<5;i++){
			JRadioButton rb = new JRadioButton();
			rdbts.add(rb);
			group.add(rb);
			rb.setBounds(50 , 80+ i * 50, 350, 37 );
			rb.addActionListener(this);
			contentPane.add(rb);
		}

		i = 0;
		for (String answer:this.questions.get(0).getAnswerList()) {
			JRadioButton rb = rdbts.get(i);
			rb.setText(answer + String.valueOf(0));
			i += 1;
		}

		for (int j = i;j<5;j++){
			JRadioButton rb = rdbts.get(j);
			rb.setVisible(false);
		}
	   
	      btnSaveNext.addActionListener(this);
	      btnPrev.addActionListener(this);
	      
	    btnCancle.addActionListener(this);

		
	}

	public void actionPerformed(ActionEvent e) {



					if(e.getActionCommand() == btnSaveNext.getActionCommand()) {
						// get the answer
						Integer answerInt = 0;
						for(JRadioButton rdb:rdbts) {
							if(rdb.isSelected()) {
								group.clearSelection();
								break;
							}
							answerInt += 1;
						}

						// save the answer or change the existing answer
						try {
							answers.get(currentAt);
							answers.set(currentAt, answerInt);
						}catch(IndexOutOfBoundsException ex) {
							answers.add(answerInt);
						}
						if(currentAt == questions.size() - 1) {
							// close uI and submit the answer
							TreeMap<Question,String> qa = new TreeMap<Question,String>();
							for(int i=0;i < questions.size();i++) {
								Question q = questions.get(i);
								String a = String.valueOf(rdbts.get(answers.get(i)).getText());
								qa.put(q, a);

							}
							StudentAction.addAnswerToAssignment(student, assignment, qa);
							Integer result = StudentAction.validateAnswer(qa);
							StudentAction.saveMark(student,assignment,result);
							lblNewLabel.setText("Your result: " + result);
							Font f = lblNewLabel.getFont();
							lblNewLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize()+4));
							for (int j = 0;j<5;j++){
								JRadioButton rb = rdbts.get(j);
								rb.setVisible(false);
								contentPane.remove(rb);
							}
							btnPrev.setEnabled(false);
							btnSaveNext.setEnabled(false);
							// display all the answers
							// setting all answers
							GridLayout grid = new GridLayout(0,1);
							grid.setVgap(10);
							JPanel panel = new JPanel(grid);
							int k;
							for ( k = 0; k < questions.size(); k++) {
								Question q = this.questions.get(k);
								lblNewLabel = new JLabel(String.valueOf(k+1) + ": " + q.getQuestion());
								panel.add(lblNewLabel);
								lblNewLabel = new JLabel("    Answer: " + q.getCorrectAnswer());
								panel.add(lblNewLabel);

							}
							panel.revalidate();

							scrollPane = new JScrollPane(panel);

							scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
							scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
							scrollPane.setBounds(35, 80, 380, 200);
							scrollPane.getViewport().setMinimumSize(new Dimension(380, 200));
							scrollPane.getViewport().setPreferredSize(new Dimension(380, 200));

							contentPane.add(scrollPane);
						}else {



							currentAt += 1;
							// trying to see if we have next answer
							try {
								answers.get(currentAt);
							}catch(IndexOutOfBoundsException ex) {
								btnSaveNext.setEnabled(false);
							}
							// rendering the next question and answers
							lblNewLabel.setText( String.valueOf(currentAt+1)+". " + questions.get(currentAt).getQuestion());
							int i = 0;
							for (String answer:questions.get(currentAt).getAnswerList()) {
								JRadioButton rb = rdbts.get(i);
								rb.setText(answer);
								rb.setVisible(true);
								i += 1;
							}
							for (int j = i;j<5;j++){
								JRadioButton rb = rdbts.get(j);
								rb.setVisible(false);
							}
							// setting anwser for rendered question if we have
							try {
								int answer =  answers.get(currentAt);
								rdbts.get(answer).setSelected(true);
							}catch(IndexOutOfBoundsException ex) {

							}
							// setting rendering rang
							btnPrev.setEnabled(true);
							// see if we have reach the end
							if(currentAt == questions.size() - 1) {
								btnSaveNext.setText("Submit");
							}
						}


					}else if(e.getActionCommand() == btnPrev.getActionCommand()) {
						if (currentAt == questions.size() - 1) {
							btnSaveNext.setText("Save & Next");
						}
						currentAt -= 1;



						lblNewLabel.setText(String.valueOf(currentAt + 1) + ". " + questions.get(currentAt).getQuestion());
						int i = 0;
						for (String answer:questions.get(currentAt).getAnswerList()) {
							JRadioButton rb = rdbts.get(i);
							rb.setText(answer);
							i += 1;
						}
						for (int j = i;j<5;j++){
							JRadioButton rb = rdbts.get(j);
							rb.setVisible(false);
						}
						// trying to see if we have prev answer, and set it
						try {
							int answer = answers.get(currentAt);
							rdbts.get(answer).setSelected(true);
							btnSaveNext.setEnabled(true);
						} catch (IndexOutOfBoundsException ex) {
						}

						if (currentAt == 0) {
							btnPrev.setEnabled(false);

						}
					}else if (e.getActionCommand()==btnCancle.getActionCommand()){
						dispose();
					}else{
						btnSaveNext.setEnabled(true);
					}
		}




}

