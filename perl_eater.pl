package main;

use Tokumori;

$tkmr = new Tokumori 595,842;

#print "Content-type: application/pdf\n";
#print "\n";

# /------------------------

$tkmr->openPDF;

for($i=1;$i<=10;$i++){

$tkmr->drawString('Hello',100,100,20,1,1,0,0,0);
$tkmr->drawRect(100,100,20,100,0.3,0,0,0,0);

$tkmr->drawString('World',100,200,20,0,0,0,0,0);
$tkmr->drawRect(100,200,100,20,0.1,0,0,0,0);


$tkmr->drawString('a-b-c-d-f',300,100,20,1,1,0.5,0.5,0.5);
$tkmr->drawString("page is $i",330,130,20,0,0,0.5,0.5,0.5);
$tkmr->drawLine(300,100,200,200,0.3,0.5,0.5,0.5);
$tkmr->drawRect(300,150,50,75,0.3,0,0.5,0.5,0.5);
$tkmr->newPage;
}
$tkmr->closePDF;


