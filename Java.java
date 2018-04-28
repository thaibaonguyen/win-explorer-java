package Phung_thi_huyen_trang;



import java.io.*;
import java.util.Date;
import java.awt.*;  
import java.awt.event.*;  
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.*;

/***********************************/  
class Java extends JPanel implements ActionListener  
{  
	private JTextField tf;
	private JTree tree;  
	private JButton refresh; 
	private JButton copy;
	private JButton paste;
	private JTable table;  
	private JScrollPane scrollTree;
	private JScrollPane scrollTable;
	private TreePath tp;
	private FileInputStream fileInputStream;
	private FileOutputStream fileOutputStream;
	private int i;
	private String sname;
	private ListSelectionListener listSelectionListener;
  
	private String[] colHeads={"File Name","SIZE(in Bytes)","Last Modified","Hidden"};  
	private String[][]data={{"","","","",""}};  
  
	public Java(String path)  
	{  
  
		tf=new JTextField();
		tf.setText("D:\\");
		
        table=new JTable(data, colHeads);  
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(listSelectionListener);
        listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                int row = table.getSelectionModel().getLeadSelectionIndex();
//                RowSorter sorter = table.getRowSorter();
//                if ( sorter != null ) {
//                    row = sorter.convertRowIndexToModel( row );                            
//                }
//                sname=((FileTableM)table.getModel()).getFile(row, 0);
                System.out.println("row: "+row); // Test row 
                sname=row+"";
            }
        };
        scrollTable=new JScrollPane(table);
		
		refresh=new JButton("Refresh");
		copy=new JButton("Copy");
		copy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//					fileInputStream=new FileInputStream(sname);
				System.out.println("Test sname"+sname); //Test sname 
				
			}
		});
		paste=new JButton("Paste");
		paste.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					fileOutputStream=new FileOutputStream(tf.getText());
					try {
						while((i=fileInputStream.read())!=-1)
						{
							fileOutputStream.write(i);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (FileNotFoundException e1) {
					
					e1.printStackTrace();
				}
				
			}
		});
  
		File temp=new File(path);  
		DefaultMutableTreeNode top=createTree(temp);  
  
		tree=new JTree(top); 
		scrollTree=new JScrollPane(tree);  
  
		final String[] colHeads={"File Name","SIZE(in Bytes)","Last Modified","Hidden"};
		String[][]data={{"","","",""}}; 
		
		
  
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());
//		panel.setBorder(getBorder());
//		panel.add(refresh,BorderLayout.CENTER);
		panel.add(paste,BorderLayout.WEST);
		panel.add(copy,BorderLayout.EAST);
		setLayout(new BorderLayout());
		add(tf,BorderLayout.NORTH);  
		add(scrollTree,BorderLayout.WEST);  
		add(scrollTable,BorderLayout.CENTER);  
		add(panel,BorderLayout.SOUTH);
		
		tree.addMouseListener(  
				new MouseAdapter()  
				{  
					public void mouseClicked(MouseEvent me)  
					{  
						doMouseClicked(me);  
					}  
				});  
		tf.addActionListener(this);  
//		refresh.addActionListener(this); 
	}  
 
	public void actionPerformed(ActionEvent ev)  
	{  
		File temp=new File(tf.getText());  
		DefaultMutableTreeNode newtop=createTree(temp);  
		if(newtop!=null)  
			tree=new JTree(newtop);  
		remove(scrollTree);  
		scrollTree=new JScrollPane(tree);  
		setVisible(false);  
		add(scrollTree,BorderLayout.WEST);  
		tree.addMouseListener(  
				new MouseAdapter()  
				{  
					public void mouseClicked(MouseEvent me)  
					{  
						doMouseClicked(me);  
					}
				});  
  
		setVisible(true);  
	}
  
	DefaultMutableTreeNode createTree(File temp)  
	{  
		DefaultMutableTreeNode top=new DefaultMutableTreeNode(temp.getPath());  
		if(!(temp.exists() && temp.isDirectory()))  
			return top;  
  
		fillTree(top,temp.getPath());  
  
		return top;
	}  
  
	void fillTree(DefaultMutableTreeNode root, String filename)  
	{  
		File temp=new File(filename);  
  
		if(!(temp.exists() && temp.isDirectory()))  
			return; 
		File[] filelist=temp.listFiles();  
  
		for(int i=0; i<filelist.length; i++)  
		{  
			if(!filelist[i].isDirectory())  
				continue;
			DefaultMutableTreeNode tempDmtn=new DefaultMutableTreeNode(filelist[i].getName());  
			root.add(tempDmtn);  
			String newfilename=new String(filename+"\\"+filelist[i].getName());  
			try {
				Thread t=new Thread()  
				{  
					public void run()  
					{  
						fillTree(tempDmtn,newfilename); 
						
					}
				}; 
				t.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void doMouseClicked(MouseEvent me)  
	{	  
		tp=tree.getPathForLocation(me.getX(),me.getY());  
		if(tp==null) return;    
		String s=tp.toString();  
		s=s.replace("[","");  
		s=s.replace("]","");  
		s=s.replace(", ","\\");   
		tf.setText(s);
		showFiles(s);  
	}  

	void showFiles(String filename)  
	{  
		File temp=new File(filename);  
		data=new String[][]{{"","","",""}};  
		remove(scrollTable);  
		table=new JTable(data, colHeads);  
		scrollTable=new JScrollPane(table);  
		setVisible(false);  
		add(scrollTable,BorderLayout.CENTER);  
		setVisible(true);  

		if(!temp.exists()) return;
		if(!temp.isDirectory()) return;
		File[] filelist=temp.listFiles();
		int fileCounter=0;
		data=new String[filelist.length][4];
		for(int i=0; i<filelist.length; i++)
		{  
			if(filelist[i].isDirectory())  
				continue;  
			data[fileCounter][0]=new String(filelist[i].getName());  
			data[fileCounter][1]=new String(filelist[i].length()+"");  
			data[fileCounter][2]=new String(new Date(filelist[i].lastModified()).toString());  
			data[fileCounter][3]=new String(filelist[i].isHidden()+"");
			fileCounter++;  
		}
  
		String dataTemp[][]=new String[fileCounter][4];  
		for(int k=0; k<fileCounter; k++)  
			dataTemp[k]=data[k];  
		data=dataTemp;  
		remove(scrollTable);  
		table=new JTable(data, colHeads);  
		scrollTable=new JScrollPane(table);  
		setVisible(false);  
		add(scrollTable,BorderLayout.CENTER);  
		setVisible(true);  
	}
}
class FileTableM extends AbstractTableModel{

    private File[][] files;
    private FileSystemView fileSystemV = FileSystemView.getFileSystemView();

    FileTableM() {
        this(new File[0][0]);
    }

    FileTableM(File[][] files) {
        this.files = files;
        
    }

    public Object getValueAt(int column, int row) {
        File file = files[column][row];
        switch (column) {
        	case 0:
        		return fileSystemV.getSystemDisplayName(file);
        	case 1:
        		return file.length();
        	case 2:
        		return file.lastModified();
        	case 3:
        		return file.isHidden();
            
        }
        return fileSystemV.getSystemDisplayName(file);
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
        	case 0:return String.class;
            case 1:
                return Long.class;
            case 3:
                return Boolean.class;
            case 2:
                return Date.class;
        }
        return String.class;
    }

    public int getRowCount() {
        return files.length;
    }

    public File getFile(int row,int column) {
        return files[row][column];
    }

    public void setFiles(File[][] files) {
        this.files = files;
        fireTableDataChanged();
    }

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}

class FileExplorer extends JFrame
{  
  
	FileExplorer (String path)  
	{  
		super("File Exploder");  
		add(new Java(path),"Center");  
		setDefaultCloseOperation(EXIT_ON_CLOSE);  
		setSize(600,600);
		setVisible(true);
	}  
  
	public static void main(String[] args)  
	{  
		new FileExplorer("D:\\");  
	}  
}  
