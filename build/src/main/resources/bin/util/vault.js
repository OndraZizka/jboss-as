// Not tested, just a sketch.

// FileSystem ref see http://msdn.microsoft.com/en-us/library/x9kfyt6a%28v=vs.85%29.aspx


function warn(x) {
    WScript.Echo("vault.js: " + x);
    // To get name of the script, we could use WScript.ScriptFullName.
}

function die( x ){
    warn(x);
    WScript.Quit(1);
}



var wsh = WScript.CreateObject("WScript.Shell");



// Setup JBOSS_HOME
var fs = WScript.CreateObject("Scripting.FileSystemObject");
var JBOSS_HOME = fs.GetFolder("..\\..").Path;



// Find java executable.
var sysEnv = wsh.Environment("SYSTEM"); // Scope.
var JAVA      = WScript.Echo( sysEnv("JAVA") );
var JAVA_HOME = WScript.Echo( sysEnv("JAVA_HOME") );

if( JAVA == "" ){
    if( JAVA_HOME != "" )
        JAVA = JAVA_HOME + "\\bin\\java";
    else
        JAVA = "java";
}

// TODO - get from env var.
if( MODULEPATH == "" ){
    MODULEPATH = JBOSS_HOME + "\\modules";
}

/*
 * Setup the JBoss Vault Tool classpath
 */

// Shared libs
// TODO: List all .jar's?
var JBOSS_VAULT_CLASSPATH = MODULEPATH + "\\org\\picketbox\\main\\*";
JBOSS_VAULT_CLASSPATH += ";" + MODULEPATH + "\\org\\jboss\\logging\\main\\*";
JBOSS_VAULT_CLASSPATH += ";" + MODULEPATH + "\\org\\jboss\\common-core\\main\\*";
JBOSS_VAULT_CLASSPATH += ";" + MODULEPATH + "\\org\\jboss\\as\\security\\main\\*";

// TODO - set env var and test whether it's passed.
//export JBOSS_VAULT_CLASSPATH
var procVars = WshShell.Environment("PROCESS");
procVars("JBOSS_VAULT_CLASSPATH") = JBOSS_VAULT_CLASSPATH; // TODO - test if this works.



// Display our environment
WScript.Echo( "=========================================================================" );
WScript.Echo( "" );
WScript.Echo( "  JBoss Vault" );
WScript.Echo( "" );
WScript.Echo( "  JBOSS_HOME: $JBOSS_HOME" );
WScript.Echo( "" );
WScript.Echo( "  JAVA: $JAVA" );
WScript.Echo( "" );
WScript.Echo( "  VAULT Classpath: $JBOSS_VAULT_CLASSPATH" );
WScript.Echo( "=========================================================================" );
WScript.Echo( "" );


var cmd = JAVA + " -classpath " + JBOSS_VAULT_CLASSPATH + ";org.jboss.as.security.vault.VaultTool"
var proc = wsh.Exec( cmd );

// Loop which prints the stdout. TODO: Add strerr. 
while( true ){
     if( proc.StdOut.AtEndOfStream ){
         if( proc.Status > 0 )  break; // Process finished.
         WScript.sleep(10);   // Wait for output otherwise.
         continue;
     }
     WScript.Echo( proc.StdOut.ReadLine() );     
}
