@set CLASSPATH=$install.root$\$rendsite.jar.location$;$install.root$\$rendsite.software.location$
@set RENDSITE_ROOT=$install.root$
@$java.jdk$\bin\java -Xmx256m rendsite.commands.Render $install.root$ %1 %2 %3 %4 %5 %6 %7 %8 %9 


