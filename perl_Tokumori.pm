package Tokumori;

binmode(STDOUT);

@offsetarray =();
$CRLF = pack("C*", 0x0D, 0x0A);  
$LF = pack("C*", 0x0A);  
$CR = pack("C*", 0x0D);  
$contentLength = 0;
$leng = 0;
$contentstream;
$pageNum = 0;

sub new {
	my $this = shift;
	my ( $width, $height ) = @_ ;
	my $tkmr = 
		{"width" => $width,
		 "height" => $height
		 };
	bless $tkmr,$this;
	return $tkmr;
}

sub drawString{
	$this = shift ;
	my $string =shift;
	my $x =shift;
	my $y =shift;
	my $fontsize =shift;
	my $isGothic =shift;
	my $isVertical =shift;
	my $r =shift;
	my $g =shift;
	my $b =shift;

	my $contentstream="";

	$contentstream .= "BT" . $CRLF;
	if( $isGothic ){
		if( $isVertical ){
			$contentstream .= "/FontGothicV $fontsize Tf" . $CRLF;
		}else{
			$contentstream .= "/FontGothic $fontsize Tf" . $CRLF;
		}
	}else{
		if( $isVertical ){
			$contentstream .= "/FontMinchoV $fontsize Tf" . $CRLF;
		}else{
			$contentstream .= "/FontMincho $fontsize Tf" . $CRLF;
		}
	}
	$contentstream .= "$r $g $b rg" . $CRLF;
		if( $isVertical ){
			$x = $x + ( $fontsize/2);
			$y = $this->{height}-$y;
		}else{
			$y = $this->{height}-$y - ($fontsize * 0.853);
		}
	$contentstream .= "1 0 0 1 $x $y Tm" . $CRLF;
	$contentstream .= "<" . $this->getStringHex($string) . "> Tj" . $CRLF;
	$contentstream .= "ET" . $CRLF;
	
	$contentLength += length($contentstream);
	print $contentstream;
	
return;
}

sub drawLine{
	$this = shift ;
	my $x1 =shift;
	my $y1 =shift;
	my $x2 =shift;
	my $y2 =shift;
	my $linewidth =shift;
	my $r =shift;
	my $g =shift;
	my $b =shift;

	my $contentstream="";

	$contentstream .= "q" . $CRLF;
	$contentstream .= "1 0 0 -1 0 $this->{height} cm" . $CRLF;
	$contentstream .= "$r $g $b RG" . $CRLF;
	$contentstream .= "$linewidth w" . $CRLF;
	$contentstream .= "$x1 $y1 m" . $CRLF;
	$contentstream .= "$x2 $y2 l" . $CRLF;
	$contentstream .= "S" . $CRLF;
	$contentstream .= "Q" . $CRLF;

	# print "str = $contentstream\n";
	print $contentstream;
	$contentLength += length($contentstream);
	# print "len = $contentLength\n";

return;
}

sub drawRect{
	$this = shift ;
	my $x =shift;
	my $y =shift;
	my $width =shift;
	my $height =shift;
	my $linewidth =shift;
	my $fill =shift;
	my $r =shift;
	my $g =shift;
	my $b =shift;

	my $contentstream="";

	$contentstream .= "q" . $CRLF;
	$contentstream .= "1 0 0 -1 0 $this->{height} cm" . $CRLF;
	$contentstream .= "$linewidth w" . $CRLF;
	$contentstream .= "$x $y $width $height re" . $CRLF;
	if( $fill) {
		$contentstream .= "$r $g $b rg" . $CRLF;
		$contentstream .= "f" . $CRLF;
	}else{
		$contentstream .= "$r $g $b RG" . $CRLF;
		$contentstream .= "S" . $CRLF;
	}
	$contentstream .= "Q" . $CRLF;
	
	# print "str = $contentstream\n";
	print $contentstream;
	$contentLength += length($contentstream);
	# print "len = $contentLength\n";

	return;
}

sub openPDF{
	$this = shift ;
	$this->getHeader;
	$this->openPage;
	return;
}
sub newPage{
	$this = shift ;
	$this->closePage;
	$this->openPage;
	return;
}
sub closePDF{
	$this = shift ;
	$this->closePage;
	$this->getPages;
	$this->getFooter;
	return;
}

sub openPage{
	$this = shift ;
	# $pageNum = shift;

	$pageNum ++;
	
	my $objNum = $pageNum * 3 + 14;
	my $content = "";

	$content .= "$objNum 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Page " . $CRLF;
	$content .= "/Parent 16 0 R " . $CRLF;
	$content .= "/Resources 3 0 R " . $CRLF;
	$objNum ++;
	$content .= "/Contents $objNum 0 R " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;

	print $content;

	$leng += length($content);
	$this->addOffSet($leng);
	$content = "";
	
	$content .= "$objNum 0 obj" . $CRLF;
	$objNum++;
	$content .= "<< /Length $objNum 0 R >> " . $CRLF;
	$content .= "stream" . $CRLF;

	$contentLength = 0;
	$contentstream ="";
	$leng += length($content);
	print $content;

return;
}

sub closePage{
	$this = shift ;
	# $pageNum = shift;
	my $content = "";
	
	my $objNum = $pageNum * 3 + 14 +2;

	$content .= "endstream ". $CRLF;
	$content .= "endobj ". $CRLF;

	print $content;
	$leng += length($content);
	$leng += $contentLength;

	$this->addOffSet($leng);
	$content = "";

	$content .= "$objNum 0 obj ". $CRLF;
	$content .= "$contentLength ". $CRLF;
	$content .= "endobj ". $CRLF;

	$leng += length($content);
	$this->addOffSet($leng);
	print $content;

return;
}

sub getPages{
	$this = shift ;
	# my $pageCount =shift;
	my $count ;
	my $objNum ;

	my $content = "";

	$content .= "16 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Pages " . $CRLF;

	$content .= "/Kids [ ";
	for ($count = 1; $count <= $pageNum; $count++) {
		$objNum = $count * 3 + 14;
		$content .= "$objNum 0 R ";
	}
	$content .= "] " . $CRLF;
	$content .= "/Count $pageNum " . $CRLF;
	$content .= "/MediaBox [ 0 0 $this->{width} $this->{height} ] " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;

	print $content;
	$leng += length($content);
	$this->addOffSet($leng);

return;
}

sub getHeader{
	$this = shift ;

($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst)=localtime(time());
$year = $year +1900; $mon = $mon + 1;
$sec = substr("00$sec" ,-2,2);
$min = substr("00$min" ,-2,2);
$hour = substr("00$hour" ,-2,2);
$mday = substr("00$mday" ,-2,2);
$mon = substr("00$mon" ,-2,2);
$year = substr("0000$year" ,-4,4);

	my $content = "";

	$content .= "%PDF-1.3" . $CRLF;
	$content .= "%矣腕" . $CRLF;
	$content .= "1 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/CreationDate (D:$year$mon$mday)" . $CRLF;
	$content .= "/Title (- Tokumori -)" . $CRLF;
	$content .= "/Author (Tokumori Library / easy pdf making engine)" . $CRLF;
	$content .= "/ModDate (D:$year$mon$mday$hour$min$sec+09'00')" . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "2 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Catalog " . $CRLF;
	$content .= "/Pages 16 0 R " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "3 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/ProcSet [ /PDF /Text ] " . $CRLF;
	$content .= "/Font << /FontGothic 4 0 R /FontMincho 7 0 R /FontGothicV 10 0 R /FontMinchoV 13 0 R >> " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "4 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Font " . $CRLF;
	$content .= "/Subtype /Type0 " . $CRLF;
	$content .= "/BaseFont /#82l#82r#83S#83V#83b#83N " . $CRLF;
	$content .= "/DescendantFonts [ 5 0 R ] " . $CRLF;
	$content .= "/Encoding /90ms-RKSJ-H " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "5 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Font " . $CRLF;
	$content .= "/Subtype /CIDFontType2 " . $CRLF;
	$content .= "/BaseFont /#82l#82r#83S#83V#83b#83N " . $CRLF;
	$content .= "/WinCharSet 128 " . $CRLF;
	$content .= "/FontDescriptor 6 0 R " . $CRLF;
	$content .= "/CIDSystemInfo << /Registry (Adobe)/Ordering (Japan1)/Supplement 2 >> " . $CRLF;
	$content .= "/DW 1000 " . $CRLF;
	$content .= "/W [ 231 389 500 631 631 500 ] " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "6 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /FontDescriptor " . $CRLF;
	$content .= "/FontName /#82l#82r#83S#83V#83b#83N " . $CRLF;
	$content .= "/Flags 39 " . $CRLF;
	$content .= "/FontBBox [ -150 -147 1100 853 ] " . $CRLF;
	$content .= "/MissingWidth 507 " . $CRLF;
	$content .= "/StemV 92 " . $CRLF;
	$content .= "/StemH 92 " . $CRLF;
	$content .= "/ItalicAngle 0 " . $CRLF;
	$content .= "/CapHeight 853 " . $CRLF;
	$content .= "/XHeight 597 " . $CRLF;
	$content .= "/Ascent 853 " . $CRLF;
	$content .= "/Descent -147 " . $CRLF;
	$content .= "/Leading 0 " . $CRLF;
	$content .= "/MaxWidth 1000 " . $CRLF;
	$content .= "/AvgWidth 507 " . $CRLF;
	$content .= "/Style << /Panose <0805020b0609000000000000>>> " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "7 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Font " . $CRLF;
	$content .= "/Subtype /Type0 " . $CRLF;
	$content .= "/BaseFont /#82l#82r#96#BE#92#A9 " . $CRLF;
	$content .= "/DescendantFonts [ 8 0 R ] " . $CRLF;
	$content .= "/Encoding /90ms-RKSJ-H " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "8 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Font " . $CRLF;
	$content .= "/Subtype /CIDFontType2 " . $CRLF;
	$content .= "/BaseFont /#82l#82r#96#BE#92#A9 " . $CRLF;
	$content .= "/WinCharSet 128 " . $CRLF;
	$content .= "/FontDescriptor 9 0 R " . $CRLF;
	$content .= "/CIDSystemInfo << /Registry (Adobe)/Ordering (Japan1)/Supplement 2 >> " . $CRLF;
	$content .= "/DW 1000 " . $CRLF;
	$content .= "/W [ 231 389 500 631 631 500 ] " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "9 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /FontDescriptor " . $CRLF;
	$content .= "/FontName /#82l#82r#96#BE#92#A9 " . $CRLF;
	$content .= "/Flags 39 " . $CRLF;
	$content .= "/FontBBox [ -150 -147 1100 853 ] " . $CRLF;
	$content .= "/MissingWidth 507 " . $CRLF;
	$content .= "/StemV 92 " . $CRLF;
	$content .= "/StemH 92 " . $CRLF;
	$content .= "/ItalicAngle 0 " . $CRLF;
	$content .= "/CapHeight 853 " . $CRLF;
	$content .= "/XHeight 597 " . $CRLF;
	$content .= "/Ascent 853 " . $CRLF;
	$content .= "/Descent -147 " . $CRLF;
	$content .= "/Leading 0 " . $CRLF;
	$content .= "/MaxWidth 1000 " . $CRLF;
	$content .= "/AvgWidth 507 " . $CRLF;
	$content .= "/Style << /Panose <0805020b0609000000000000>>> " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "10 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Font " . $CRLF;
	$content .= "/Subtype /Type0 " . $CRLF;
	$content .= "/BaseFont /#82l#82r#83S#83V#83b#83N " . $CRLF;
	$content .= "/DescendantFonts [ 11 0 R ] " . $CRLF;
	$content .= "/Encoding /90ms-RKSJ-V " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "11 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Font " . $CRLF;
	$content .= "/Subtype /CIDFontType2 " . $CRLF;
	$content .= "/BaseFont /#82l#82r#83S#83V#83b#83N " . $CRLF;
	$content .= "/WinCharSet 128 " . $CRLF;
	$content .= "/FontDescriptor 12 0 R " . $CRLF;
	$content .= "/CIDSystemInfo << /Registry (Adobe)/Ordering (Japan1)/Supplement 2 >> " . $CRLF;
	$content .= "/DW 1000 " . $CRLF;
	$content .= "/W [ 231 389 500 631 631 500 ] " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "12 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /FontDescriptor " . $CRLF;
	$content .= "/FontName /#82l#82r#83S#83V#83b#83N " . $CRLF;
	$content .= "/Flags 39 " . $CRLF;
	$content .= "/FontBBox [ -150 -147 1100 853 ] " . $CRLF;
	$content .= "/MissingWidth 507 " . $CRLF;
	$content .= "/StemV 92 " . $CRLF;
	$content .= "/StemH 92 " . $CRLF;
	$content .= "/ItalicAngle 0 " . $CRLF;
	$content .= "/CapHeight 853 " . $CRLF;
	$content .= "/XHeight 597 " . $CRLF;
	$content .= "/Ascent 853 " . $CRLF;
	$content .= "/Descent -147 " . $CRLF;
	$content .= "/Leading 0 " . $CRLF;
	$content .= "/MaxWidth 1000 " . $CRLF;
	$content .= "/AvgWidth 507 " . $CRLF;
	$content .= "/Style << /Panose <0805020b0609000000000000>>> " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "13 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Font " . $CRLF;
	$content .= "/Subtype /Type0 " . $CRLF;
	$content .= "/BaseFont /#82l#82r#96#BE#92#A9 " . $CRLF;
	$content .= "/DescendantFonts [ 14 0 R ] " . $CRLF;
	$content .= "/Encoding /90ms-RKSJ-V " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "14 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /Font " . $CRLF;
	$content .= "/Subtype /CIDFontType2 " . $CRLF;
	$content .= "/BaseFont /#82l#82r#96#BE#92#A9 " . $CRLF;
	$content .= "/WinCharSet 128 " . $CRLF;
	$content .= "/FontDescriptor 15 0 R " . $CRLF;
	$content .= "/CIDSystemInfo << /Registry (Adobe)/Ordering (Japan1)/Supplement 2 >> " . $CRLF;
	$content .= "/DW 1000 " . $CRLF;
	$content .= "/W [ 231 389 500 631 631 500 ] " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;
	$content .= "15 0 obj" . $CRLF;
	$content .= "<< " . $CRLF;
	$content .= "/Type /FontDescriptor " . $CRLF;
	$content .= "/FontName /#82l#82r#96#BE#92#A9 " . $CRLF;
	$content .= "/Flags 39 " . $CRLF;
	$content .= "/FontBBox [ -150 -147 1100 853 ] " . $CRLF;
	$content .= "/MissingWidth 507 " . $CRLF;
	$content .= "/StemV 92 " . $CRLF;
	$content .= "/StemH 92 " . $CRLF;
	$content .= "/ItalicAngle 0 " . $CRLF;
	$content .= "/CapHeight 853 " . $CRLF;
	$content .= "/XHeight 597 " . $CRLF;
	$content .= "/Ascent 853 " . $CRLF;
	$content .= "/Descent -147 " . $CRLF;
	$content .= "/Leading 0 " . $CRLF;
	$content .= "/MaxWidth 1000 " . $CRLF;
	$content .= "/AvgWidth 507 " . $CRLF;
	$content .= "/Style << /Panose <0805020b0609000000000000>>> " . $CRLF;
	$content .= ">> " . $CRLF;
	$content .= "endobj" . $CRLF;

	print $content;
	$leng += length($content);
	$this->addOffSet($leng);

return;
}

sub getFooter{
	$this =shift ;
	
	my $content = "";
	$content .= "xref" . $CRLF;

	my $objCnt = 0;
	$objCnt = 16 + $#offsetarray;

	$content .= "0 $objCnt" . $CRLF;

	$content .= "0000000000 65535 f" . $CRLF;
	$content .= "0000000017 00000 n" . $CRLF;
	$content .= "0000000184 00000 n" . $CRLF;
	$content .= "0000000244 00000 n" . $CRLF;
	$content .= "0000000387 00000 n" . $CRLF;
	$content .= "0000000538 00000 n" . $CRLF;
	$content .= "0000000800 00000 n" . $CRLF;
	$content .= "0000001154 00000 n" . $CRLF;
	$content .= "0000001301 00000 n" . $CRLF;
	$content .= "0000001559 00000 n" . $CRLF;
	$content .= "0000001909 00000 n" . $CRLF;
	$content .= "0000002062 00000 n" . $CRLF;
	$content .= "0000002326 00000 n" . $CRLF;
	$content .= "0000002681 00000 n" . $CRLF;
	$content .= "0000002830 00000 n" . $CRLF;
	$content .= "0000003090 00000 n" . $CRLF;

	$content .= $this->getByteOffset($offsetarray[$#offsetarray-1]);

for ($i=0 ;$i<$#offsetarray-1;$i++){
	$content .= $this->getByteOffset($offsetarray[$i]);
}

	$content .= "trailer" . $CRLF;
	$content .= "<<" . $CRLF;
	$content .= "/Size $objCnt" . $CRLF;
	$content .= "/Info 1 0 R " . $CRLF;
	$content .= "/Root 2 0 R " . $CRLF;
	$content .= "/ID[<1e6aae289c38279857ec84160dbe482a><1e6aae289c38279857ec84160dbe482a>]" . $CRLF;
	$content .= ">>" . $CRLF;
	$content .= "startxref" . $CRLF;
	$content .= $offsetarray[$#offsetarray] . $CRLF;
	$content .= "%%EOF" . $CRLF;

	print $content;
	return;
}

sub getStringHex{
	$this = shift ;
	$string = shift ;
	my $strHex;
	@hexarray = ("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f");
	for ($i = 0; $i < length($string); $i++) {
		$char = substr($string, $i, 1);
		if ($char =~ /[\x80-\xff]/) {
			$char = substr($string, $i++, 1);
			$char1 = substr($string, $i, 1);
			$asc = ord($char);
			$asc1 = ord($char1);
			$ascH = $hexarray[int($asc / 16)];
			$ascL = $hexarray[$asc % 16];
			$asc1H = $hexarray[int($asc1 / 16)];
			$asc1L = $hexarray[$asc1 % 16];
			$strHex = $strHex . $ascH . $ascL . $asc1H . $asc1L;
			#print "[$char,$ascH,$ascL,$asc1H,$asc1L] ";
		}else{
			$asc = ord($char);
			$ascH = $hexarray[int($asc / 16)];
			$ascL = $hexarray[$asc % 16];
			$strHex = $strHex . $ascH . $ascL;
			#print "[$char,$ascH,$ascL] ";
		}
	}
	return($strHex);
}

sub addOffSet{
	$this = shift ;
	$num = shift;
	push( @offsetarray , $num);
	return;
}
sub getByteOffset{
	$this = shift ;
	my $num =shift;
	return substr("0000000000" . $num, -10, 10) . " 00000 n". $CRLF;
}

true;
