##RENDSITE

RendSite is a tool I created to catalog my portfolio for the web.  
It is based on the Things/Thinger platform.  
I looked at other tools, but it was easier just to code this.

Pardon the derpy layout.  This used to be an Eclipse project and only I
ever planned on using it.

As long as you have a jdk /bin somewhere in the path and ant installed 
(there is a brew package for it), then you should be able to build just 
by giving the command "ant" from this directory.  Also "ant package" will
create the binary distro as a zip file under release/.

On unix platforms, "and docker" should build a docker image that can 
do rendering.  There will be a convenience script at release/render.sh
to help use it.

