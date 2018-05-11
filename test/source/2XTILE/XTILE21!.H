/*

 The C language include file XTile graphics manager

 COPYRIGHT (C) 1994  Erich P Gatejen   All Rights Reserved


 File: XTILE21!.H    	Written for the Turbo C compiler


*/


/* Modes available */
enum Modes { Xmode0 = 0, Xmode1, Xmode2, Xmode3, Xmode4,
	     Xmode5, Xmode6, Xmode7, Xmode8, Xmode9, Xmode10,
	     Xmode11  };


/* Define Values									*/
#define	PAGE0		0        /* Mode 0 page values                                  */
#define	PAGE1		19200
#define	BACKGROUND	38400
#define	STORE		57600

#define	NONE		0
#define	FULL_PAGE	240
#define UPLOADFONT	0
#define DONTUPLOADFONT  1
#define DONTMASK        0
#define DOMASK		1
#define NORMALPAGE	0
#define SPLITPAGE       1

#define   MOUSEOFFSET    ( 0xffff - 80 )
#define   MOUSERELOAD    ( 0xffff - 16 )

/* Set event mask */
#define   XMOUSE_MOVE     1
#define   XLEFT_BUTTON	  2
#define   XLEFT_RELEASE   4
#define   XRIGHT_BUTTON   8
#define   XRIGHT_RELEASE  16

/* Read events masks (BX) */
#define   XLEFT_STATE     1  /* left button state 1=down */
#define   XRIGHT_STATE    2



void	XInit_Mode( int Mode );

void	XSet_Write_Page( unsigned int  PageOffset, unsigned int  XSize );

void	XSet_Display_Page( unsigned int	PageOffset,
			   unsigned int XSize,
			   unsigned int YSize,
			   unsigned int ViewX,
			   unsigned int ViewY	 );

void	XSet_Display_PageP( unsigned int    PageOffset,
			    unsigned int    XSize,
			    unsigned int    YSize,
			    unsigned int    ViewX,
			    unsigned int    ViewY,
			    unsigned int    Start,
			    unsigned int    Number,
			    unsigned char   far *Pal	      );


int  XSet_View ( unsigned int  XLoc,
	         unsigned int  YLoc		 );

void XWait_Retrace( void );

void XSet_SplitScreen(  int     NumLines );

void XClear ( unsigned char	Color );

void XSet_Box ( unsigned int	Start_X,
                unsigned int	Start_Y,
		unsigned int	Delta_X,
		unsigned int	Delta_Y,
		unsigned char	Color	);

void XPut_Tile  ( unsigned int	Start_X,
		  unsigned int	Start_Y,
		  unsigned int	Delta_X,
		  unsigned int	Delta_Y,
		  unsigned char	far	*Data );

void XPut_TileA ( unsigned int	Start_X,
		  unsigned int	Start_Y,
		  unsigned int	Delta_X,
		  unsigned int	Delta_Y,
		  unsigned char	far	*Data );


int  XTile_Size ( unsigned int	Delta_X,
                  unsigned int	Delta_Y 	);

void	XUpload_Tile (  unsigned int		Offset,
				 unsigned int		Size,
				 unsigned char far    *Data 	);

void	XUpload_Sprite ( unsigned int		Offset,
				  unsigned int		Size,
				  unsigned char far    *Data 	);

void	XPaste_Tile    (  unsigned int	Start_X,
				   unsigned int	Start_Y,
				   unsigned int	Delta_X,
				   unsigned int	Delta_Y,
				   unsigned int	Offset	);

void	XPaste_Sprite  (  unsigned int	Start_X,
				   unsigned int	Start_Y,
				   unsigned int	Delta_X,
				   unsigned int	Delta_Y,
				   unsigned int	Offset	);

void	XMove_Tile     (  unsigned int	Start_X,
				   unsigned int	Start_Y,
				   unsigned int	Delta_X,
				   unsigned int	Delta_Y,
				   unsigned int	New_X,
				   unsigned int	New_Y	);

void XPut_Tile_M    (  unsigned int	Start_X,
				   unsigned int	Start_Y,
				   unsigned int	Delta_X,
				   unsigned int	Delta_Y,
				   unsigned char	far	*Data );

void XPut_TileA_M    (  unsigned int	Start_X,
				   unsigned int	Start_Y,
				   unsigned int	Delta_X,
				   unsigned int	Delta_Y,
				   unsigned char	far	*Data );

int	XRegister_Font8 (  unsigned int		Offset,
				    unsigned int		NumChars,
				    unsigned int		Base_Char,
				    unsigned int		Upload_Flag,
				    unsigned char far    *Data 	);

void	XChar8		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int 	     Character	);

void Register_Font_Masks8 ( unsigned char	  far *Masks );

void	XChar8_M		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int 	     Character	);

void	XChar8_C		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int 	Character,
				   unsigned int		Color		);


void	XString8		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int		Mask_Flag,
				   char 	 far *String	);

void	XString8_C		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int		Color,
				   char 	 far *String	);

int	XRegister_Font4 (  unsigned int		Offset,
				    unsigned int		NumChars,
				    unsigned int		Base_Char,
				    unsigned int		Upload_Flag,
				    unsigned char far    *Data 	);

void	XChar4		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int 	     Character	);

void Register_Font_Masks4 ( unsigned char	  far *Masks );

void	XChar4_M		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int 	     Character	);


void	XChar4_C		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int 	Character,
				   unsigned int		Color	);


void	XString4		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int		Mask_Flag,
				   char 	 far *String	);


void	XString4_C		(  unsigned int		X_Loc,
				   unsigned int		Y_Loc,
				   unsigned int		Color,
				   char 	 far *String	);

void	XPaste_Tile_M  (  unsigned int	Start_X,
				   unsigned int	Start_Y,
				   unsigned int	Delta_X,
				   unsigned int	Delta_Y,
				   unsigned int	Offset,
				   unsigned char    far *Mask );

void	XPaste_Sprite_M (  unsigned int	Start_X,
			   unsigned int	Start_Y,
			   unsigned int	Delta_X,
			   unsigned int	Delta_Y,
			   unsigned int	Offset,
			   unsigned char   far *Mask );

void	XSet_Pal_Color  (  unsigned int	Color,
			   unsigned int	Red,
			   unsigned int	Green,
			   unsigned int	Blue	     );

void XSet_Pal_Block	(  unsigned int	Start,
			   unsigned int	Number,
			   unsigned char   far *Pal	      );

int	XRegister_Mouse (  unsigned char   far	*PointerData,
			   unsigned char   far *PointerMask,
			   void		   far	*UserHandler  );

void	XMouse_Active	(  unsigned int	 EventMask,
			   unsigned int	 Start_X,
			   unsigned int	 Start_Y,
			   unsigned int	 InSplit	      );

void	XMouse_InActive ( void );

void	XMouse_ActiveS	(  unsigned int	 EventMask,
			   unsigned int	 Start_X,
			   unsigned int	 Start_Y	      );

void	XMouse_InActiveS ( void );

void    XWhere_Mouse     ( unsigned int  far *X,
                           unsigned int  far *Y  );


/* Prototype for YOUR mouse handler routine.  DO NOT CALL.	*/
/* You must create the actual function					*/
void	XUser_MouseHndlr ( unsigned int	Mouse_X,
			   unsigned int	Mouse_Y,
			   unsigned int	Event_Mask  );

void XPut_Pixel ( unsigned int	X_Location,
		  unsigned int	Y_Location,
		  unsigned int	Color	     );

void XPut_Line  (  unsigned int	Start_X,
		   unsigned int	Start_Y,
		   unsigned int	End_X,
		   unsigned int	End_Y,
		   unsigned int	Color        );

void XPut_Circle ( unsigned int Center_X,
                   unsigned int Center_Y,
                   unsigned int Radius,
                   unsigned int Color        );


void XReload_TileW  (  unsigned int	Start_X,
		       unsigned int	Start_Y,
		       unsigned int	Delta_X,
		       unsigned int	Delta_Y,
		       unsigned int	Offset      );

void XDownload_TileS (  unsigned int	Offset,
			unsigned int	X_Size,
			unsigned int	Y_Size,
			unsigned char  *far Destination );

void XDownload_TileP  (  unsigned int	Start_X,
			 unsigned int	Start_Y,
			 unsigned int	Delta_X,
			 unsigned int	Delta_Y,
			 unsigned char *far Destination );


void XMove_TileA     (  unsigned int	Start_X,
				   unsigned int	Start_Y,
				   unsigned int	Delta_X,
				   unsigned int	Delta_Y,
				   unsigned int	New_X,
				   unsigned int	New_Y	);

void	XSet_AltPage( unsigned int  PageOffset, unsigned int  XSize );

void    XPut_ScaledBitmap(  unsigned int	Dest_X,
			    unsigned int	Dest_Y,
			    unsigned int	New_X,
			    unsigned int	New_Y,
			    unsigned int        BMapSize_X,
			    unsigned int        BMapSize_Y,
			    unsigned char *far Destination	);

void    XSet_Clip( 	    unsigned int	Left,
			    unsigned int	Right,
			    unsigned int	Top,
			    unsigned int	Bottom		);
