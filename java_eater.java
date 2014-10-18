//package hogehoge;

import java.io.* ;

public class eater {
	public eater(){
		init();
	}
	public static void main(String args[]){
		new eater();
	}
	public void init(){
		Tokumori tkmr = new Tokumori(new PrintStream(System.out));
		tkmr.openPDF(595,842);

		tkmr.drawString("ABCD",100,200,20,false,false,0,0,0);
		tkmr.drawRect(100,200,40,20,0.5,false,0,0,0);

		tkmr.drawString("abcd",150,200,20,false,true,0,0,0);
		tkmr.drawRect(150,200,20,40,0.5,false,0,0,0);

		tkmr.drawString("EFGH",180,200,20,true,false,0,0,0);
		tkmr.drawRect(180,200,40,20,0.5,false,0,0,0);

		tkmr.drawString("efghs",230,200,20,true,true,0,0,0);
		tkmr.drawRect(230,200,20,40,0.5,false,0,0,0);

		//tkmr.newPage();
		//}

		tkmr.closePDF();

	}

}
