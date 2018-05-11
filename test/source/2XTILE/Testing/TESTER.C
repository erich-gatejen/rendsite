#include <stdlib.h>
#include <conio.h>
#include "xtile21!.h"
#include <stdio.h>
#include <alloc.h>
#include <dos.h>


#define	ALLBUTTONS	30
#define ALLBANDREDRAW  31

#define RELOAD		(STORE + (1680/4) + 1)

#define	AFTERSPLIT	3200

#define	TRUE	1
#define	FALSE	0


int	Size, Color = 1;

int   LastX = -1;
int   LastY = -1;
float Slp   = 0;


FILE		*test;
char*     far  mpoint;
char*	far  mmask;
char*	far  mask1;
char*	far  pal;
char* 	far	font;
char*	far  fmask;

char*	far hex;

int  	Value;

int X, Y;
unsigned int	SplitMFlag	= NORMALPAGE;

/*void	XUser_MouseHndlr (	unsigned int	Mouse_X,
					unsigned int	Mouse_Y,
					unsigned int	Event_Mask  ) {

   if ( SplitMFlag == SPLITPAGE )    XSet_Write_Page( PAGE0, 400 );

   XMouse_InActive();

   XPaste_Tile( 0, 0, 8, 8, MOUSERELOAD );

   XSet_Box( Mouse_X, Mouse_Y, 4, 4, 10 );
   XSet_Write_Page( AFTERSPLIT, 400 );

   XMouse_Active(  XLEFT_BUTTON, Mouse_X, Mouse_Y, SplitMFlag );
}; */
/*
void	XUser_MouseHndlr (	unsigned int	Mouse_X,
					unsigned int	Mouse_Y,
					unsigned int	Event_Mask  ) {

   int X;

   XMouse_InActiveS();

   XPaste_Tile( 0, 0, 8, 8, MOUSERELOAD );


   if ( Mouse_X > 209 ) X = Mouse_X -209;
   else
      X = Mouse_X;

   XSet_Box( X, Mouse_Y, 4, 4, 10 );
   XSet_Write_Page( AFTERSPLIT, 400 );

   XMouse_ActiveS(  XLEFT_BUTTON, Mouse_X, Mouse_Y );
};

*/


void main() {


	char		What[20];
	int		Mode;
	char		Command;

	printf("What mode : ");
	gets( What );
	Mode = atoi( What );

	test  = fopen("othello.pal", "rb");
	pal  = farmalloc( 768 );
	fread( pal, sizeof(char), 768, test);
	fclose( test );

/*	test  = fopen("mpoint2.spi", "rb");
	mpoint  = farmalloc( 256 );
	fread( mpoint, sizeof(char), 256, test);
	fclose( test );

	test  = fopen("mpoint2.spm", "rb");
	mmask  = farmalloc( 64 );
	fread( mmask, sizeof(char), 64, test);
	fclose( test );
*/
	test  = fopen("board.vga", "rb");
	hex  = farmalloc( 16134 );
	fread( hex, sizeof(char), 16134, test);
	fclose( test );

 /*	test  = fopen("test4.fnt", "rb");
	font  = farmalloc( 168 );
	fread( font, sizeof(char), 168, test);
	fclose( test );

	test  = fopen("test4.msf", "rb");
	fmask  = farmalloc( 24 );
	fread( fmask, sizeof(char), 24, test);
	fclose( test );

*/
	XClear(0);

	XInit_Mode( Mode );

	XClear(0);


	XSet_Display_PageP( PAGE0, 320, 240, 0, 0, 0, 255, pal );
	XSet_Write_Page(   PAGE0, 320 );

	X = 168;
	Y = 96;

	XSet_Clip( 4, 316, 4, 236 );
	Command = getch();
	while( (Command != 'q') && (Command != 'Q') ) {

	   XSet_Box( 0, 0, 320, 240, 0 );
	   XPut_ScaledBitmap( 4, 4, X, Y, 168, 96, hex+6 );

	   switch( Command ) {

	      case 'u' : Y++;
			 break;

	      case 'm' : Y--;
			 break;

	      case 'h' : X++;
			 break;

	      case 'k' : Y--;
			 break;

	      case 'j' : X = 168;
			 Y = 96;
			 break;
	   };

	   Command = getch();
	};

	/*
	XSet_SplitScreen( 30 );
	getch();

*/



/*
	getch();
	XPut_Tile( 16, 10, 24, 17, hex );
	XPut_Tile( 17, 29, 24, 17, hex );
	XPut_Tile( 18, 48, 24, 17, hex );
	XPut_Tile( 19, 67, 24, 17, hex );

	getch();
	XPut_TileA( 48, 10, 24, 17, hex );
	XPut_TileA( 49, 29, 24, 17, hex );
	XPut_TileA( 50, 48, 24, 17, hex );
	XPut_TileA( 51, 67, 24, 17, hex );

	getch();
	XPut_Tile_M( 80, 10, 24, 17, hex );

	getch();
	XPut_TileA_M( 112, 10, 24, 17, hex );
	XPut_TileA_M( 113, 29, 24, 17, hex );
	XPut_TileA_M( 114, 48, 24, 17, hex );
	XPut_TileA_M( 115, 67, 24, 17, hex );


	XUpload_Sprite( STORE, 1680, mpoint );

	XPaste_Tile( 0,0,20,21, STORE);
	getch();


	XReload_TileW( 0,0,20,21, RELOAD);

	do {

	   XPaste_Sprite_M( X, Y, 20, 21, STORE, mmask );

	   XWait_Retrace();

	   XPaste_Tile( X, Y, 20, 21, RELOAD );

	   X = X + 2;
	   Y++;

	   XReload_TileW( X, Y, 20, 21, RELOAD );

	} while (X < 321 );

	getch();

	XMove_Tile( 100, 100, 16, 16, 40, 40 );
	getch();
	XMove_Tile( 100, 100, 16, 16, 100, 150);
	getch();
	XMove_Tile( 100, 100, 16, 16, 40, 100);
	getch();
	XMove_Tile( 100, 100, 16, 16, 160, 100);
	getch();
	XMove_Tile( 100, 100, 16, 16, 108, 108);
	*/

	/* Test fonts */
/*	XRegister_Font4( STORE, 7, 'A', DONTUPLOADFONT, font );
	XChar4(   100, 10, 'B');
	XChar4_M( 100, 25, 'B');
	getch();

	XString4( 100, 50, DONTMASK, "ABCDEFG");
	getch();
	XString4( 100, 65, DOMASK, "ABCDEFG");

	getch();

	XRegister_Font4( STORE, 7, 'A', UPLOADFONT, font );
	XChar4(   100, 80, 'A');
	getch();
	XString4( 100, 100, DONTMASK, "ABCDEFG");
	Register_Font_Masks4( fmask );
	getch();
	XChar4_M( 200, 115, 'A');

	getch();
	XString4( 100, 130, DOMASK, "ABCDEFG");
	getch();

	XString4_C( 200, 130, 5, "ABCDEFG");
	getch();

*/
/*	XRegister_Mouse( mpoint, mmask, XUser_MouseHndlr );

	   XPaste_Sprite_M( 120, 20, 8, 8, MOUSEOFFSET, mmask );
	   XPaste_Sprite_M( 121, 30, 8, 8, MOUSEOFFSET, mmask );
	   XPaste_Sprite_M( 122, 40, 8, 8, MOUSEOFFSET, mmask );
	   XPaste_Sprite_M( 123, 50, 8, 8, MOUSEOFFSET, mmask );
*/
/*	getch();

	XMouse_Active( XLEFT_BUTTON, 64, 64, SplitMFlag );

	getch();

	XMouse_InActive();

	SplitMFlag = SPLITPAGE;

	XMouse_Active( XLEFT_BUTTON, 8, 8, SplitMFlag );

	getch();

	XMouse_InActive();

*/
/*	getch();

	XMouse_ActiveS( XLEFT_BUTTON, 100, 207 );

	getch();

	XMouse_InActiveS();

	getch();

	XMouse_ActiveS( XLEFT_BUTTON, 100, 220 );

	getch();

	XMouse_InActiveS();

	getch();


	XSet_View( 12, 12 );
	getch();
	XSet_View( 16, 16 );
	getch();
	XSet_View( 20, 20 );
	getch();
	XSet_View( 24, 24 );
	getch();

*/




};

