#include <stdlib.h>
#include <conio.h>
#include "xtile20!.h"
#include <stdio.h>
#include <alloc.h>
#include <dos.h>


#define RELOAD		(STORE + (1680/4) + 1)

#define	AFTERSPLIT	3200

#define	TRUE	1
#define	FALSE	0


FILE		*test;
void*	far  buf;
void*     far  buf1;
char*     far  mpoint;
char*	far  mmask;
char*	far  mask1;
char*	far  pal;
char* 	far	font;
char*	far  fmask;

char*	far hex;
char*	far download;
char*	far download2;

int  	Value;

int X, Y;
unsigned int	SplitMFlag	= NORMALPAGE;

void main( int argn, char* argc[] ) {


	char		What[20];
	int		Mode;

	Mode = atoi( argc[1] );

	test  = fopen("thudd.pix", "rb");
	buf   = farmalloc( 64000 );
	fread( buf, sizeof(char), 64000, test);
	fclose( test );

	test  = fopen("thudd2.pix", "rb");
	buf1   = farmalloc( 12800 );
	fread( buf1, sizeof(char), 12800, test);
	fclose( test );

	test  = fopen("mpoint.spi", "rb");
	mpoint  = farmalloc( 480 );
	fread( mpoint, sizeof(char), 480, test);
	fclose( test );

	test  = fopen("mpoint.spm", "rb");
	mmask  = farmalloc( 120 );
	fread( mmask, sizeof(char), 120, test);
	fclose( test );

	test  = fopen("1.pix", "rb");
	hex  = farmalloc( 408 );
	download  = farmalloc( 408 );
	download2  = farmalloc( 408 );
	fread( hex, sizeof(char), 408, test);
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


	XSet_Display_Page( AFTERSPLIT, 400, 300, 0, 0 );
	XSet_Write_Page(   AFTERSPLIT, 400 );

/*	XSet_Box( 0, 0, 320, 240, 1 );


	getch();
	XPut_TileA( 16, 10, 24, 17, hex );
	XPut_TileA( 17, 29, 24, 17, hex );
	XPut_TileA( 18, 48, 24, 17, hex );
	XPut_TileA( 19, 67, 24, 17, hex );

	getch();
*/
/*	getch();
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

	getch();
*/

/*	XUpload_Sprite( STORE, 480, mpoint );


	XReload_TileW( 0,0,12,10, RELOAD);

	do {

	   XPaste_Sprite_M( X, Y, 12, 10, STORE, mmask );

	   XWait_Retrace();

	   XPaste_Tile( X, Y, 12, 10, RELOAD );

	   X = X + 2;
	   Y++;

	   XReload_TileW( X, Y, 12, 10, RELOAD );

	} while (X < 321 );
*/
/*	getch();

	XReload_TileW( 16, 10, 24, 17, RELOAD );

	XPaste_Tile ( 100, 10, 24, 17, RELOAD );


	getch();

	XDownload_TileS( RELOAD, 24, 17, download );

	XPut_Tile( 140, 10, 24, 17, download );

	getch();

	XDownload_TileP( 16, 10, 24, 17, download2 );
	XPut_Tile( 180, 10, 24, 17, download2 );
	XDownload_TileP( 17, 29, 24, 17, download2 );
	XPut_Tile( 180, 29, 24, 17, download2 );
	XDownload_TileP( 18, 48, 24, 17, download2 );
	XPut_Tile( 180, 48, 24, 17, download2 );
	XDownload_TileP( 19, 67, 24, 17, download2 );
	XPut_Tile( 180, 67, 24, 17, download2 );

	getch();

*/
	XPut_Line( 10, 10, 10, 200,   1 );
	XPut_Line( 10, 10, 200, 10,   1 );
	XPut_Line( 10, 200, 200, 200, 1 );
	XPut_Line( 200, 10, 200, 200, 1 );

	XPut_Line( 10, 10, 200, 200, 2 );
	XPut_Line( 10, 10, 200, 100, 2 );
	XPut_Line( 10, 10, 100, 200, 2 );

	getch();

};

