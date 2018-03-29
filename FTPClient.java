/*
 * GET filename.txt
 * PUT filename.txt
 * quit
 */



//FTPClient

import java.net.*;
import java.io.*;
import java.util.*;


class FTPClient
{
    public static void main(String args[]) throws Exception
    {
        Socket soc=new Socket("127.0.0.1",6788);
        ClientTranfer t=new ClientTranfer(soc);
        t.displayMenu();

    }
}
class ClientTranfer
{
    Socket ClientSoc;
    DataInputStream din;
    DataOutputStream dout;
    BufferedReader br;
    public ClientTranfer(Socket s)
    {
    	ClientSoc=s;
        try
        {
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            br=new BufferedReader(new InputStreamReader(System.in));
        }
        catch(Exception ex)
        {
        	System.out.println("Connection Error ...");
        }
    }

    void SendFile(String fileName) throws Exception
    {

        File f=new File(fileName);
        if(!f.exists())
        {
            System.out.println("File not Exists...");
            dout.writeUTF("File not found");
            return;
        }


        String msgFromServer=din.readUTF();
        if(msgFromServer.compareTo("File Already Exists")==0)
        {
            String Option;
            System.out.println("File Already Exists. Want to OverWrite (Y/N) ?");
            Option=br.readLine();
            if(Option=="Y")
            {
                dout.writeUTF("Y");
            }
            else
            {
                dout.writeUTF("N");
                return;
            }
        }

        System.out.println("Sending File ...");
        FileInputStream fin=new FileInputStream(f);
        int ch;
        do
        {
            ch=fin.read();
            dout.writeUTF(String.valueOf(ch));
        }
        while(ch!=-1);
        fin.close();
        System.out.println(din.readUTF());
        }



    void ReceiveFile(String fileName) throws Exception
    {
        dout.writeUTF(fileName);
        String msgFromServer=din.readUTF();

        if(msgFromServer.compareTo("File Not Found")==0)
        {
            System.out.println("File not found on Server ...");
            return;
        }
        else
        {
            System.out.println("Receiving File ...");
            File f=new File(fileName);

            FileOutputStream fout=new FileOutputStream(f);
            int count = -1;
	   	     byte[] buffer = new byte[8192];
	          while ((count = din.read(buffer)) > 0)
	          {

  		      fout.write(buffer, 0, count);
                if (count < 8192) break;
	          }


            fout.close();
            System.out.println("File Receiving successfull ");

        }
    }

    public void displayMenu() throws Exception
    {
        while(true)
        {

           System.out.println("Enter command in the following format:");
           System.out.println("-> GET <filename>");
           System.out.println("-> PUT <filename>");
           System.out.println("-> quit");
            String command=br.readLine();
            StringTokenizer st=new StringTokenizer(command," ");
            String s1=st.nextToken();

            if(s1.equals("GET"))
            {
            	String fn=st.nextToken();
                dout.writeUTF(command);
                ReceiveFile(fn);
            }
            else if(s1.equals("PUT"))
            {
            	String fn=st.nextToken();
                dout.writeUTF(command);
                SendFile(fn);
            }

            else if(s1.equals("quit"))
            {
            	String s2=s1;
                dout.writeUTF(s2);
                System.exit(1);
            }

        }
    }
}
