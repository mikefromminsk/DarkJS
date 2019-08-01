[Setup]
AppId={{780CC627-5B3B-4E12-AC5D-6DD9B7BCA1DA} 

AppName=Darkjs
AppVersion=1.0
;AppVerName=My Program 1.5
AppPublisher=My Company, Inc.
AppPublisherURL=http://www.example.com/
AppSupportURL=http://www.example.com/
AppUpdatesURL=http://www.example.com/

DefaultDirName=C:\Darkjs
DisableDirPage=yes
DefaultGroupName=Darkjs
OutputDir=C:\Users\mgaiduk\Desktop\DarkJS\dist
OutputBaseFilename=darkjs-setup
Compression=lzma
SolidCompression=yes

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Icons]
Name: "{group}\My Program"; Filename: "{app}\run.bat"

[Files]
;Source: "c:\temp\ewrg\jre-8u221-windows-x64.exe"; DestDir: "{tmp}"; DestName: "JREInstaller.exe"; Flags: deleteafterinstall; AfterInstall: RunJavaInstaller(); Check: IsWin64 AND InstallJava64();
                                                                     
Source: "C:\wamp\www\droid\*"; DestDir: "{app}\root"; Flags: ignoreversion recursesubdirs;
Source: "C:\Users\mgaiduk\Desktop\DarkJS\out\artifacts\DarkJs_jar\nssm.exe"; DestDir: "{app}";
Source: "C:\Users\mgaiduk\Desktop\DarkJS\out\artifacts\DarkJs_jar\DarkJs.jar"; DestDir: "{app}";

[run]
Filename: {app}\nssm.exe; Parameters: "install darkjs c:\Progra~1\Java\jre1.8.0_221\bin\java.exe -jar {app}\darkjs.jar > {app}\darkjs.log"
Filename: {app}\nssm.exe; Parameters: "start darkjs"

[UninstallRun]                               
Filename: {app}\nssm.exe; Parameters: "stop darkjs"
Filename: {app}\nssm.exe; Parameters: "remove darkjs"

[UninstallDelete]
Type: filesandordirs; Name: {app}

[Code]

procedure DecodeVersion(verstr: String; var verint: array of Integer);
var
  i,p: Integer; s: string;
begin
  { initialize array }
  verint := [0,0,0,0];
  i := 0;
  while ((Length(verstr) > 0) and (i < 4)) do
  begin
    p := pos ('.', verstr);
    if p > 0 then
    begin
      if p = 1 then s:= '0' else s:= Copy (verstr, 1, p - 1);
      verint[i] := StrToInt(s);
      i := i + 1;
      verstr := Copy (verstr, p+1, Length(verstr));
    end
    else
    begin
      verint[i] := StrToInt (verstr);
      verstr := '';
    end;
  end;
end;

function CompareVersion (ver1, ver2: String) : Integer;
var
  verint1, verint2: array of Integer;
  i: integer;
begin
  SetArrayLength (verint1, 4);
  DecodeVersion (ver1, verint1);

  SetArrayLength (verint2, 4);
  DecodeVersion (ver2, verint2);

  Result := 0; i := 0;
  while ((Result = 0) and ( i < 4 )) do
  begin
    if verint1[i] > verint2[i] then
      Result := 1
    else
      if verint1[i] < verint2[i] then
        Result := -1
      else
        Result := 0;
    i := i + 1;
  end;
end;

function InstallJava() : Boolean;
var
  ErrCode: Integer;
  JVer: String;
  InstallJ: Boolean;
begin
  RegQueryStringValue(
    HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', JVer);
  InstallJ := true;
  if Length( JVer ) > 0 then
  begin
    if CompareVersion(JVer, '1.8') >= 0 then
    begin
      InstallJ := false;
    end;
  end;
  Result := InstallJ;
end;

function InstallJava64() : Boolean;
var
  ErrCode: Integer;
  JVer: String;
  InstallJ: Boolean;
begin
  RegQueryStringValue(
    HKLM64, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', JVer);
  InstallJ := true;
  if Length( JVer ) > 0 then
  begin
    if CompareVersion(JVer, '1.8') >= 0 then
    begin
      InstallJ := false;
    end;
  end;
  Result := InstallJ;
end;

procedure RunJavaInstaller();
var
  StatusText: string;
  ResultCode: Integer;
  Path, Parameters: string;
begin
  Path := '{tmp}\JREInstaller.exe';
  { http://docs.oracle.com/javase/8/docs/technotes/guides/install/config.html#table_config_file_options }
  Parameters := '/s INSTALL_SILENT=Enable REBOOT=Disable SPONSORS=Disable REMOVEOUTOFDATEJRES=1';
  StatusText:= WizardForm.StatusLabel.Caption;
  WizardForm.StatusLabel.Caption:='Installing the java runtime environment. Wait a moment ...';
  WizardForm.ProgressGauge.Style := npbstMarquee;
  try
    if not Exec(ExpandConstant(Path), Parameters, '', SW_SHOW, ewWaitUntilTerminated, ResultCode) then
    begin
      { we inform the user we couldn't install the JRE }
      MsgBox('Java runtime environment install failed with error ' + IntToStr(ResultCode) + 
        '. Try installing it manually and try again to install MyProg.', mbError, MB_OK);
    end;
  finally
    WizardForm.StatusLabel.Caption := StatusText;
    WizardForm.ProgressGauge.Style := npbstNormal;
  end;
end;









{TODO add input login pass nodename }

var
  CustomQueryPage: TInputQueryWizardPage;

procedure AddCustomQueryPage();
begin
  CustomQueryPage := CreateInputQueryPage(
    wpWelcome,
    'Custom message',
    'Custom description',
    'Custom instructions');

  { Add items (False means it's not a password edit) }
  CustomQueryPage.Add('Custom Field:', False);
end;

procedure InitializeWizard();
begin
  //AddCustomQueryPage();
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
  begin
    { Read custom value }
    MsgBox('Custom Value = ' + CustomQueryPage.Values[0], mbInformation, MB_OK);
  end;
end;
