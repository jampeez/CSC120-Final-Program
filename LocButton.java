package yardSaleApp;
import javax.swing.JButton;
import javax.swing.JFrame;

public class LocButton extends JFrame{
	JButton b1;
	LocButton(){
		 b1 = new JButton("Allow");
		b1.setBounds(200,100,100,50);
		b1.addActionListener(e -> this.dispose());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setSize(500,500);
		this.setVisible(true);
		this.add(b1);
}
}
