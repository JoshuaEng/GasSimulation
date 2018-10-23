package main;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ButtonDisplay extends JPanel implements ActionListener{

	private JTextField numberField;
	private JTextField xField;
	private JTextField yField;
	//private JButton reset;

	ButtonDisplay(){
		super();
		setLayout(new GridLayout(0,4));
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		numberField = new JTextField();
		JLabel numberName = new JLabel("New Number of Balls:");
		numberName.setLabelFor(numberField);
		numberField.addActionListener(this);
		add(numberName);
		add(numberField);


		xField = new JTextField();
		JLabel xName = new JLabel("New X Dimension:");
		xName.setLabelFor(xField);
		xField.addActionListener(this);
		add(xName);
		add(xField);

		yField = new JTextField();
		JLabel yName = new JLabel("New Y Dimension:");
		yName.setLabelFor(yField);
		yField.addActionListener(this);
		add(yName);
		add(yField);


		JButton reset = new JButton("Reset");
		reset.addActionListener(this);
		add(reset);




	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//This is ok because all done after the window renders. Will end up killing own thread after boolean set, but thats ok
		String newNum = numberField.getText();
		String newX = xField.getText();
		String newY = yField.getText();
		try {
			int num = Integer.parseInt(newNum);
			if(num > 0 & num <= 1000) {
				Constants.NUM_BALLS.set(num);
			}
		}
		catch(Exception er){}
		try {
			int x = Integer.parseInt(newX);
			if(x > 0 && x < Toolkit.getDefaultToolkit().getScreenSize().width) {
				Constants.X_MAX.set(x);
			}
		}
		catch(Exception er){}
		try {
			int y = Integer.parseInt(newY);
			if(y > 0 && y < Toolkit.getDefaultToolkit().getScreenSize().height - Constants.Y_MAX_GRAPH) {
				Constants.Y_MAX_SIM.set(y); //these do not actually have to be atomic. Only the boolean
			}
		}
		catch(Exception er){}
		
		Constants.reset.set(true);
	}

}

