//package hogehoge;

import java.io.* ;
import java.util.Vector;
import java.util.zip.DeflaterOutputStream;

public class Tokumori {

		byte bCRLF[] = {(byte)0x0d,(byte)0x0a};
		String CRLF = new String(bCRLF);

		//byte bMAMO[] = {(byte)0xE2,(byte)0xE3,(byte)0xCF,(byte)0xD3};
		byte bMAMO[] = {(byte)0x20,(byte)0x20,(byte)0x20,(byte)0x20};
		String MAMO = new String(bMAMO);

		int xPDF;
		int yPDF;

		long offsetlength;
		long contentLength;
		int pageNum;
		PrintStream out;

		ByteArrayOutputStream stream;
		DeflaterOutputStream dos;
		ByteArrayOutputStream b;

		Vector offset = new Vector();

	public Tokumori(PrintStream _out){
		offsetlength = 0 ; //1
		pageNum = 0;
		out = _out;
	}

	public void drawString(
		String str,
		double x,
		double y,
		int fontsize,
		boolean isGothic,
		boolean isVertical,
		double r,
		double g,
		double b 
	){
		StringBuffer contentstream = new StringBuffer();

		contentstream.append("BT" +CRLF) ;
		if( isGothic ){
			if( isVertical ){
				contentstream.append("/FontGothicV " + fontsize + " Tf" +CRLF) ;
			}else{
				contentstream.append("/FontGothic " + fontsize + " Tf" +CRLF) ;
			}
		}else{
			if( isVertical ){
				contentstream.append("/FontMinchoV " + fontsize + " Tf" +CRLF) ;
			}else{
				contentstream.append("/FontMincho " + fontsize + " Tf" +CRLF) ;
			}
		}
		contentstream.append(r + " " + g + " " + b  +" rg" +CRLF) ;
		if( isVertical ){
			x = x + (fontsize/2);
			y = yPDF-y;
		}else{
			y = yPDF-y - (fontsize * 0.853);
		}
		contentstream.append("1 0 0 1 " + x + " " + y +" Tm" +CRLF) ;
		contentstream.append("<" + this.getStringHex(str) + "> Tj" +CRLF) ;
		contentstream.append("ET" +CRLF) ;

		contentLength += contentstream.length();
		//out.print(contentstream.toString());
		//out.flush();
		try{
		stream.write(contentstream.toString().getBytes(),0,contentstream.length());
		}catch(Exception e){
		}

	}

	public void drawLine(
		double x1,
		double y1,
		double x2,
		double y2,
		double linewidth,
		double r,
		double g,
		double b 
	){
		StringBuffer contentstream = new StringBuffer();

		contentstream.append("q" +CRLF) ;
		contentstream.append("1 0 0 -1 0 " +yPDF+ " cm" +CRLF) ;
		contentstream.append(r + " " + g + " " + b  +" RG" +CRLF) ;
		contentstream.append(linewidth + " w" +CRLF) ;
		contentstream.append(x1 +" " + y1+ " m" +CRLF) ;
		contentstream.append(x2 +" " + y2+ " l" +CRLF) ;
		contentstream.append("S" +CRLF) ;
		contentstream.append("Q" +CRLF) ;

		contentLength += contentstream.length();
		//out.print(contentstream.toString());
		//out.flush();
		try{
		stream.write(contentstream.toString().getBytes(),0,contentstream.length());
		}catch(Exception e){
		}
	}

	public void drawRect(
		double x,
		double y,
		double width,
		double height,
		double linewidth,
		boolean fill,
		double r,
		double g,
		double b 
	){
		StringBuffer contentstream = new StringBuffer();

		contentstream.append("q" +CRLF) ;
		contentstream.append("1 0 0 -1 0 " +yPDF+ " cm" +CRLF) ;
		contentstream.append(linewidth+ " w" +CRLF) ;
		contentstream.append(x + " " + y + " " + width  + " " + height + " re" +CRLF) ;
		if(fill) {
			contentstream.append(r + " " + g + " " + b  +" rg" +CRLF) ;
			contentstream.append("f" +CRLF) ;
		}else{
			contentstream.append(r + " " + g + " " + b  +" RG" +CRLF) ;
			contentstream.append("S" +CRLF) ;
		}
		contentstream.append("Q" +CRLF) ;
		
		contentLength += contentstream.length();
		//out.print(contentstream.toString());
		//out.flush();
		try{
		stream.write(contentstream.toString().getBytes(),0,contentstream.length());
		}catch(Exception e){
		}

	}


	public void openPDF(int xPDF,int yPDF){
		this.xPDF = xPDF;
		this.yPDF = yPDF;
		this.getHeader();
		this.openPage();
	}
	public void newPage(){
		this.closePage();
		this.openPage();
	}
	public void closePDF(){
		this.closePage();
		this.getPages();
		this.getFooter();
	}

	private void openPage(){
		StringBuffer sb = new StringBuffer();
		pageNum ++;
		int objNum =pageNum * 3 + 14;

		sb.append(objNum + " 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Page " +CRLF) ;
		sb.append("/Parent 16 0 R " +CRLF) ;
		sb.append("/Resources 3 0 R " +CRLF) ;
		objNum ++;
		sb.append("/Contents " + objNum + " 0 R " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		
		out.print(sb.toString());
		out.flush();
		offsetlength += sb.length();
		this.addOffSet(offsetlength);
		sb = new StringBuffer();

		sb.append(objNum +" 0 obj" +CRLF) ;
		objNum++;
		sb.append("<< /Filter /FlateDecode /Length " + objNum + " 0 R >> " +CRLF) ;
		sb.append("stream" +CRLF) ;

		contentLength = 0;
		//contentstream = new StringBuffer();

		out.print(sb.toString());
		out.flush();
		offsetlength += sb.length();
		//this.addOffSet(offsetlength);

		stream=new ByteArrayOutputStream();
	}

	private void closePage(){
		StringBuffer sb = new StringBuffer();
		int objNum = pageNum * 3 + 14 +2;
		
		ByteArrayOutputStream b=new ByteArrayOutputStream();
		dos = new DeflaterOutputStream(b);
		try{
		stream.writeTo(dos);
		dos.finish();
		dos.close();
		b.writeTo(out);
		}catch(Exception e){
		}

		sb.append("endstream " +CRLF) ;
		sb.append("endobj " +CRLF) ;
		out.print(sb.toString());
		out.flush();
		offsetlength += sb.length();

		contentLength = b.size();

		offsetlength += contentLength;
		this.addOffSet(offsetlength);

		sb = new StringBuffer();
		sb.append(objNum + " 0 obj " +CRLF) ;
		sb.append(contentLength + " " +CRLF) ;
		sb.append("endobj " +CRLF) ;

		out.print(sb.toString());
		out.flush();
		offsetlength += sb.length();
		this.addOffSet(offsetlength);
	}

	private void getPages(){
		StringBuffer sb = new StringBuffer();
		int count;
		int objNum;

		sb.append("16 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Pages " +CRLF) ;

		sb.append("/Kids [ ");
		for (count = 1; count <= pageNum; count++) {
			objNum = count * 3 + 14;
				sb.append(objNum + " 0 R ");
		}
		sb.append("] " +CRLF) ;
		sb.append("/Count " + pageNum + " " +CRLF) ;
		sb.append("/MediaBox [ 0 0 "+ xPDF +" " +yPDF+ " ] " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;

		out.print(sb.toString());
		out.flush();

		offsetlength += sb.length();
		this.addOffSet(offsetlength);

	}


	private void getHeader(){
		StringBuffer sb = new StringBuffer();

		sb.append("%PDF-1.3" +CRLF) ;
		sb.append("%" + MAMO +CRLF) ;
		sb.append("1 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/CreationDate (D:20020616)" +CRLF) ;
		sb.append("/Title (- Tokumori -)" +CRLF) ;
		sb.append("/Author (MATSUO Yuji, Recruit Media Communications)" +CRLF) ;
		sb.append("/ModDate (D:20020616170448+09'00')" +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("2 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Catalog " +CRLF) ;
		sb.append("/Pages 16 0 R " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("3 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/ProcSet [ /PDF /Text ] " +CRLF) ;
		sb.append("/Font << /FontGothic 4 0 R /FontMincho 7 0 R /FontGothicV 10 0 R /FontMinchoV 13 0 R >> " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("4 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Font " +CRLF) ;
		sb.append("/Subtype /Type0 " +CRLF) ;
		sb.append("/BaseFont /#82l#82r#83S#83V#83b#83N " +CRLF) ;
		sb.append("/DescendantFonts [ 5 0 R ] " +CRLF) ;
		sb.append("/Encoding /90ms-RKSJ-H " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("5 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Font " +CRLF) ;
		sb.append("/Subtype /CIDFontType2 " +CRLF) ;
		sb.append("/BaseFont /#82l#82r#83S#83V#83b#83N " +CRLF) ;
		sb.append("/WinCharSet 128 " +CRLF) ;
		sb.append("/FontDescriptor 6 0 R " +CRLF) ;
		sb.append("/CIDSystemInfo << /Registry (Adobe)/Ordering (Japan1)/Supplement 2 >> " +CRLF) ;
		sb.append("/DW 1000 " +CRLF) ;
		sb.append("/W [ 231 389 500 631 631 500 ] " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("6 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /FontDescriptor " +CRLF) ;
		sb.append("/FontName /#82l#82r#83S#83V#83b#83N " +CRLF) ;
		sb.append("/Flags 39 " +CRLF) ;
		sb.append("/FontBBox [ -150 -147 1100 853 ] " +CRLF) ;
		sb.append("/MissingWidth 507 " +CRLF) ;
		sb.append("/StemV 92 " +CRLF) ;
		sb.append("/StemH 92 " +CRLF) ;
		sb.append("/ItalicAngle 0 " +CRLF) ;
		sb.append("/CapHeight 853 " +CRLF) ;
		sb.append("/XHeight 597 " +CRLF) ;
		sb.append("/Ascent 853 " +CRLF) ;
		sb.append("/Descent -147 " +CRLF) ;
		sb.append("/Leading 0 " +CRLF) ;
		sb.append("/MaxWidth 1000 " +CRLF) ;
		sb.append("/AvgWidth 507 " +CRLF) ;
		sb.append("/Style << /Panose <0805020b0609000000000000>>> " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("7 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Font " +CRLF) ;
		sb.append("/Subtype /Type0 " +CRLF) ;
		sb.append("/BaseFont /#82l#82r#96#BE#92#A9 " +CRLF) ;
		sb.append("/DescendantFonts [ 8 0 R ] " +CRLF) ;
		sb.append("/Encoding /90ms-RKSJ-H " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("8 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Font " +CRLF) ;
		sb.append("/Subtype /CIDFontType2 " +CRLF) ;
		sb.append("/BaseFont /#82l#82r#96#BE#92#A9 " +CRLF) ;
		sb.append("/WinCharSet 128 " +CRLF) ;
		sb.append("/FontDescriptor 9 0 R " +CRLF) ;
		sb.append("/CIDSystemInfo << /Registry (Adobe)/Ordering (Japan1)/Supplement 2 >> " +CRLF) ;
		sb.append("/DW 1000 " +CRLF) ;
		sb.append("/W [ 231 389 500 631 631 500 ] " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("9 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /FontDescriptor " +CRLF) ;
		sb.append("/FontName /#82l#82r#96#BE#92#A9 " +CRLF) ;
		sb.append("/Flags 39 " +CRLF) ;
		sb.append("/FontBBox [ -150 -147 1100 853 ] " +CRLF) ;
		sb.append("/MissingWidth 507 " +CRLF) ;
		sb.append("/StemV 92 " +CRLF) ;
		sb.append("/StemH 92 " +CRLF) ;
		sb.append("/ItalicAngle 0 " +CRLF) ;
		sb.append("/CapHeight 853 " +CRLF) ;
		sb.append("/XHeight 597 " +CRLF) ;
		sb.append("/Ascent 853 " +CRLF) ;
		sb.append("/Descent -147 " +CRLF) ;
		sb.append("/Leading 0 " +CRLF) ;
		sb.append("/MaxWidth 1000 " +CRLF) ;
		sb.append("/AvgWidth 507 " +CRLF) ;
		sb.append("/Style << /Panose <0805020b0609000000000000>>> " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("10 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Font " +CRLF) ;
		sb.append("/Subtype /Type0 " +CRLF) ;
		sb.append("/BaseFont /#82l#82r#83S#83V#83b#83N " +CRLF) ;
		sb.append("/DescendantFonts [ 11 0 R ] " +CRLF) ;
		sb.append("/Encoding /90ms-RKSJ-V " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("11 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Font " +CRLF) ;
		sb.append("/Subtype /CIDFontType2 " +CRLF) ;
		sb.append("/BaseFont /#82l#82r#83S#83V#83b#83N " +CRLF) ;
		sb.append("/WinCharSet 128 " +CRLF) ;
		sb.append("/FontDescriptor 12 0 R " +CRLF) ;
		sb.append("/CIDSystemInfo << /Registry (Adobe)/Ordering (Japan1)/Supplement 2 >> " +CRLF) ;
		sb.append("/DW 1000 " +CRLF) ;
		sb.append("/W [ 231 389 500 631 631 500 ] " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("12 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /FontDescriptor " +CRLF) ;
		sb.append("/FontName /#82l#82r#83S#83V#83b#83N " +CRLF) ;
		sb.append("/Flags 39 " +CRLF) ;
		sb.append("/FontBBox [ -150 -147 1100 853 ] " +CRLF) ;
		sb.append("/MissingWidth 507 " +CRLF) ;
		sb.append("/StemV 92 " +CRLF) ;
		sb.append("/StemH 92 " +CRLF) ;
		sb.append("/ItalicAngle 0 " +CRLF) ;
		sb.append("/CapHeight 853 " +CRLF) ;
		sb.append("/XHeight 597 " +CRLF) ;
		sb.append("/Ascent 853 " +CRLF) ;
		sb.append("/Descent -147 " +CRLF) ;
		sb.append("/Leading 0 " +CRLF) ;
		sb.append("/MaxWidth 1000 " +CRLF) ;
		sb.append("/AvgWidth 507 " +CRLF) ;
		sb.append("/Style << /Panose <0805020b0609000000000000>>> " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("13 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Font " +CRLF) ;
		sb.append("/Subtype /Type0 " +CRLF) ;
		sb.append("/BaseFont /#82l#82r#96#BE#92#A9 " +CRLF) ;
		sb.append("/DescendantFonts [ 14 0 R ] " +CRLF) ;
		sb.append("/Encoding /90ms-RKSJ-V " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("14 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /Font " +CRLF) ;
		sb.append("/Subtype /CIDFontType2 " +CRLF) ;
		sb.append("/BaseFont /#82l#82r#96#BE#92#A9 " +CRLF) ;
		sb.append("/WinCharSet 128 " +CRLF) ;
		sb.append("/FontDescriptor 15 0 R " +CRLF) ;
		sb.append("/CIDSystemInfo << /Registry (Adobe)/Ordering (Japan1)/Supplement 2 >> " +CRLF) ;
		sb.append("/DW 1000 " +CRLF) ;
		sb.append("/W [ 231 389 500 631 631 500 ] " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;
		sb.append("15 0 obj" +CRLF) ;
		sb.append("<< " +CRLF) ;
		sb.append("/Type /FontDescriptor " +CRLF) ;
		sb.append("/FontName /#82l#82r#96#BE#92#A9 " +CRLF) ;
		sb.append("/Flags 39 " +CRLF) ;
		sb.append("/FontBBox [ -150 -147 1100 853 ] " +CRLF) ;
		sb.append("/MissingWidth 507 " +CRLF) ;
		sb.append("/StemV 92 " +CRLF) ;
		sb.append("/StemH 92 " +CRLF) ;
		sb.append("/ItalicAngle 0 " +CRLF) ;
		sb.append("/CapHeight 853 " +CRLF) ;
		sb.append("/XHeight 597 " +CRLF) ;
		sb.append("/Ascent 853 " +CRLF) ;
		sb.append("/Descent -147 " +CRLF) ;
		sb.append("/Leading 0 " +CRLF) ;
		sb.append("/MaxWidth 1000 " +CRLF) ;
		sb.append("/AvgWidth 507 " +CRLF) ;
		sb.append("/Style << /Panose <0805020b0609000000000000>>> " +CRLF) ;
		sb.append(">> " +CRLF) ;
		sb.append("endobj" +CRLF) ;

		offsetlength += sb.length();
		this.addOffSet(offsetlength);
		//System.out.println("length " + sb.length());
		out.print(sb.toString());
		out.flush();
	}

	private void getFooter(){
		StringBuffer sb = new StringBuffer();
		sb.append("xref" +CRLF) ;
		int objCnt = 0;
		objCnt = 15 + offset.size();
		sb.append("0 "+ objCnt +CRLF) ;
		sb.append("0000000000 65535 f" +CRLF) ;
		sb.append("0000000017 00000 n" +CRLF) ;
		sb.append("0000000184 00000 n" +CRLF) ;
		sb.append("0000000244 00000 n" +CRLF) ;
		sb.append("0000000387 00000 n" +CRLF) ;
		sb.append("0000000538 00000 n" +CRLF) ;
		sb.append("0000000800 00000 n" +CRLF) ;
		sb.append("0000001154 00000 n" +CRLF) ;
		sb.append("0000001301 00000 n" +CRLF) ;
		sb.append("0000001559 00000 n" +CRLF) ;
		sb.append("0000001909 00000 n" +CRLF) ;
		sb.append("0000002062 00000 n" +CRLF) ;
		sb.append("0000002326 00000 n" +CRLF) ;
		sb.append("0000002681 00000 n" +CRLF) ;
		sb.append("0000002830 00000 n" +CRLF) ;
		sb.append("0000003090 00000 n" +CRLF) ;

		out.print(sb.toString());
		out.flush();

		this.getOffset();

		sb = new StringBuffer();

		sb.append("trailer" +CRLF) ;
		sb.append("<<" +CRLF) ;
		sb.append("/Size " + objCnt +CRLF) ;
		sb.append("/Info 1 0 R " +CRLF) ;
		sb.append("/Root 2 0 R " +CRLF) ;
		sb.append("/ID[<1e6aae289c38279857ec84160dbe482a><1e6aae289c38279857ec84160dbe482a>]" +CRLF) ;
		sb.append(">>" +CRLF) ;
		sb.append("startxref" +CRLF) ;
		sb.append( ((Long)offset.get(offset.size()-1)).longValue() +CRLF);
		sb.append("%%EOF" +CRLF) ;

		out.print(sb.toString());
		out.flush();
	}

	private String getStringHex(String str){
		StringBuffer hex;
		StringBuffer strhex = new StringBuffer();
		try{
			byte bStr[] = str.getBytes("Shift_JIS");
			for(int i = 0 ;i<bStr.length;i++){
				hex = new StringBuffer();
				hex.append( (new Integer(0)).toHexString(bStr[i]) );
				strhex.append(hex.substring(hex.length()-2));
			}
			}catch(Exception e){
				System.err.println("err "+e);
			}
		return strhex.toString();
	}

	private void addOffSet(long _offset){
		offset.add(new Long(_offset));
	}

	private void getOffset(){
		StringBuffer sb ;
		long os ;

			os =((Long)offset.get(offset.size()-2)).longValue();
			sb = new StringBuffer();
			sb.append("0000000000");
			sb.append(os);
			out.print((sb.substring(sb.length()-10)) + " 00000 n" + CRLF);

		for(int i=0;i<=offset.size()-3;i++){
			os =((Long)offset.get(i)).longValue();
			sb = new StringBuffer();
			sb.append("0000000000");
			sb.append(os);
			out.print((sb.substring(sb.length()-10)) + " 00000 n" + CRLF);
			out.flush();
		}
	}

}
