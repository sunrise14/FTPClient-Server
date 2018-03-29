/*
 * GET filename.txt
 * PUT filename.txt
 * quit
 */


//FTPServer


import java.net.*;
import java.io.*;
import java.util.*;

public class FTPServer
{
    public static void main(String args[]) throws Exception
    {
        ServerSocket serverSocket=new ServerSocket(6788);
        System.out.println("FTP Server Started on Port Number 6789");
        while(true)
        {
            System.out.println("Waiting for Connection ...");
            ServerSocket clientSocket=serverSocket;
            Transferfile t=new Transferfile(clientSocket.accept());

        }
    }
}

class Transferfile extends Thread
{
    Socket socket;

    DataInputStream din;
    DataOutputStream dout;

    public Transferfile(Socket soc)
    {
        try
        {
            socket=soc;
            din=new DataInputStream(socket.getInputStream());
            dout=new DataOutputStream(socket.getOutputStream());
            System.out.println("FTP Client Connected ...");
            start();

        }
        catch(Exception ex)
        {
        	System.out.println("Connection Error ...");
        }
    }

    void ReceiveFromClient(String fileName) throws Exception
    {
        if(fileName.compareTo("File not found")==0)
        {
            return;
        }
        File f=new File(fileName);
        String option;

        if(f.exists())
        {
            dout.writeUTF("File Already Exists");
            option=din.readUTF();
        }
        else
        {
            dout.writeUTF("SendFile");
            option="Y";
        }

            if(option.compareTo("Y")==0)
            {
                FileOutputStream fout=new FileOutputStream(f);
                int ch;
                String temp;
                do
                {
                    temp=din.readUTF();
                    ch=Integer.parseInt(temp);
                    if(ch!=-1)
                    {
                        fout.write(ch);
                    }
                }while(ch!=-1);
                fout.close();
                dout.writeUTF("File Sent Successfully to Server");
                System.out.println("File Sent Successfull to Server");
            }
            else
            {
                return;
            }

    }

    void SendToClient(String fileName) throws Exception
    {

        File f=new File(fileName);
        if(!f.exists())
        {
            dout.writeUTF("File Not Found");
            return;
        }
        else
        {
            dout.writeUTF("READY");
            FileInputStream fin=new FileInputStream(f);
            byte[] buffer = new byte[8192];
            int count=-1;
	          while ((count = fin.read(buffer)) > 0)
	          {
                System.out.println("count is \n" + count);
  		          dout.write(buffer, 0, count);
	          }

            fin.close();
            System.out.println("File Received at Server");

        }
    }

    void listFiles() throws IOException
    {
    	String filePath=System.getProperty("user.dir");
    	System.out.println(filePath);
    	dout.writeUTF(filePath);


    }

    public void run()
    {
        while(true)
        {
            try
            {
            System.out.println("Waiting for Command ...");
            String Command=din.readUTF();


            StringTokenizer st=new StringTokenizer(Command," ");
            String s1=st.nextToken();
            if(s1.equals("ls"))
            {
            	String s2=null;
            	s2=s1;
            }
            if(s1.equals("quit"))
            {
            	String s2=null;
            	s2=s1;
            }

            if(s1.equals("GET"))
            {
            	String fn=st.nextToken();
                System.out.println("\tGET Command Received ...");
                SendToClient(fn);
                continue;
            }
            else if(s1.equals("PUT"))
            {
            	String fn=st.nextToken();
                System.out.println("\tPUT Command Receiced ...");
                ReceiveFromClient(fn);
                continue;
            }

            else if(s1.equals("quit"))
            {
                System.out.println("\tDisconnect Command Received ...");
                System.exit(1);
            }

            }
            catch(Exception ex)
            {
            	System.out.println("Connection Error ...");
            }
        }
    }
}
