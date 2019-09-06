package com.jpaint;

import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.Hashtable;

class ManagerMenus {
private ImageModel theModel;
private JFrame mainFrame;
private Hashtable<String, MenuItem> menuItems;
private ManagerFiles managerFiles;

ManagerMenus(ImageModel imageModel, JFrame jFrame, ManagerFiles fileManager) {
	theModel = imageModel;
	mainFrame = jFrame;
	managerFiles = fileManager;
	
	//create the menu
	JMenuBar menuBar = new JMenuBar();
	mainFrame.setJMenuBar(menuBar);
	
	JMenu fileMenu = new JMenu("File");
	menuBar.add(fileMenu);
	
	JMenu editMenu = new JMenu("Edit");
	menuBar.add(editMenu);
	
	JMenu imageMenu = new JMenu("Image");
	menuBar.add(imageMenu);
	
	JMenu helpMenu = new JMenu("Help");
	menuBar.add(helpMenu);
	
	
	/*====== MENU ITEMS ======*/
	
	menuItems = new Hashtable<>();
	
	int cmdCtrlModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	int cmdCtrlShiftModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK;
	
	//file menu
	menuItems.put("new", new MenuItem("New Opaque Image", KeyEvent.VK_N, cmdCtrlModifier, fileMenu, KeyEvent.VK_N,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					managerFiles.newFile(false);
				}
			}));
	
	menuItems.put(
		  "newtrans",
		  new MenuItem("New Transparent Image", KeyEvent.VK_N, cmdCtrlShiftModifier, fileMenu, KeyEvent.VK_T,
				  new ActionListener() {
					  @Override
					  public void actionPerformed(ActionEvent e) {
						  managerFiles.newFile(true);
					  }
				  }));
	fileMenu.addSeparator();
	
	menuItems.put("open", new MenuItem("Open", KeyEvent.VK_O, cmdCtrlModifier, fileMenu, KeyEvent.VK_O,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					managerFiles.openFile();
				}
			}));
	menuItems.put("save", new MenuItem("Save", KeyEvent.VK_S, cmdCtrlModifier, fileMenu, KeyEvent.VK_S,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					managerFiles.save();
				}
			}));
	menuItems.put("saveas", new MenuItem("Save As", KeyEvent.VK_S, cmdCtrlShiftModifier, fileMenu, KeyEvent.VK_A,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					managerFiles.saveas();
				}
			}));
	fileMenu.addSeparator();
	
	menuItems
		  .put("print", new MenuItem("Print", KeyEvent.VK_P, cmdCtrlModifier, fileMenu, KeyEvent.VK_P,
				  new ActionListener() {
					  @Override
					  public void actionPerformed(ActionEvent e) {
						  print();
					  }
				  }
		  ));
	fileMenu.addSeparator();
	
	if (Main.IS_MAC)
		menuItems.put("close", new MenuItem("Close", KeyEvent.VK_W, cmdCtrlModifier, fileMenu, KeyEvent.VK_W,
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						managerFiles.exit();
					}
				}
		));
	else
		menuItems.put("exit", new MenuItem("Exit", KeyEvent.VK_E, cmdCtrlModifier, fileMenu, KeyEvent.VK_E,
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						managerFiles.exit();
					}
				}
		));
	
	//edit menu
	menuItems.put("undo", new MenuItem("Undo", KeyEvent.VK_Z, cmdCtrlModifier, editMenu, KeyEvent.VK_Z,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					undo();
				}
			}
	));
	
	MenuItem redoItem; //shortcut changes depending on platform
	if (Main.IS_MAC)
		redoItem = new MenuItem("Redo", KeyEvent.VK_Z, cmdCtrlShiftModifier, editMenu, KeyEvent.VK_Z,
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						redo();
					}
				});
	else redoItem = new MenuItem("Redo", KeyEvent.VK_Y, cmdCtrlModifier, editMenu, KeyEvent.VK_Y,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					redo();
				}
			});
	menuItems.put("redo", redoItem);
	
	editMenu.addSeparator();
	
	menuItems.put("selectall", new MenuItem("Select All", KeyEvent.VK_A, cmdCtrlModifier, editMenu, KeyEvent.VK_A,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dummy();
				}
			}
	));
	editMenu.addSeparator();
	
	menuItems.put("cut", new MenuItem("Cut", KeyEvent.VK_X, cmdCtrlModifier, editMenu, KeyEvent.VK_X,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dummy();
				}
			}
	));
	menuItems.put("copy", new MenuItem("Copy", KeyEvent.VK_C, cmdCtrlModifier, editMenu, KeyEvent.VK_C,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dummy();
				}
			}
	));
	menuItems.put("paste", new MenuItem("Paste", KeyEvent.VK_V, cmdCtrlModifier, editMenu, KeyEvent.VK_V,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dummy();
				}
			}
	));
	
	//image menu
	menuItems.put("resize", new MenuItem("Resize", imageMenu, KeyEvent.VK_R,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					resize();
				}
			}));
	imageMenu.addSeparator();
	
	menuItems.put("fliph", new MenuItem("Flip Image Horizontally", imageMenu, KeyEvent.VK_H,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					flip(true);
				}
			}));
	menuItems.put("flipv", new MenuItem("Flip Image Vertically", imageMenu, KeyEvent.VK_V,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					flip(false);
				}
			}
	));
	imageMenu.addSeparator();
	
	menuItems.put("rotateleft", new MenuItem("Rotate Left 90\u00B0", imageMenu, KeyEvent.VK_L,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					rotate(Canvas.Transform.ROTATE_LEFT);
				}
			}
	));
	menuItems.put("rotateright", new MenuItem("Rotate Right 90\u00B0", imageMenu, KeyEvent.VK_R,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					rotate(Canvas.Transform.ROTATE_RIGHT);
				}
			}
	));
	menuItems.put("rotate180", new MenuItem("Rotate 180\u00B0", imageMenu, KeyEvent.VK_U,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					rotate(Canvas.Transform.ROTATE_180);
				}
			}
	));

	menuItems.put("about", new MenuItem("About " + WindowApplication.APPLICATION_NAME, helpMenu, KeyEvent.VK_U,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Image image = null;
					try {
						image = ImageIO.read(getClass().getResource("icons/appicon.png"));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					try {
						if (image != null) {
							image = image.getScaledInstance(116, 116, java.awt.Image.SCALE_SMOOTH);
						}
					} catch (NullPointerException ignored) {
					}
					ImageIcon appIcon;
					if (image == null) appIcon = null;
					else appIcon = new ImageIcon(image);

					JOptionPane.showMessageDialog(mainFrame, WindowApplication.APPLICATION_NAME +
									" is a fun painting program \n" +
									"inspired by Apple's MacPaint (from \n" +
									"1984) and old versions of Microsoft \n" +
									"Paint. Created by Andrew Yaros in \n" +
									"2019, originally for the CS 338 \n" +
									"(GUIs) class at Drexel University.",
							"About " + WindowApplication.APPLICATION_NAME, JOptionPane.INFORMATION_MESSAGE,
							appIcon
					);
				}
			}));
	
}

//cross platform print dialog based on java example
private void print() {
	PrinterJob printJob = PrinterJob.getPrinterJob();
	printJob.setPrintable(new Printable() {
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
			if (pageIndex != 0) {
				return Printable.NO_SUCH_PAGE;
			}
			graphics.drawImage(theModel.getImage(), 0, 0, theModel.getWidth(), theModel.getHeight(), null);
			return Printable.PAGE_EXISTS;
		}
	});

	PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
	printJob.pageDialog(attributeSet);
	boolean isOK = printJob.printDialog(attributeSet);
	if (isOK) try { printJob.print(attributeSet); } catch (PrinterException e) { e.printStackTrace(); }
}


//undo/redo
private void undo() { theModel.undo(); }

private void redo() { theModel.redo(); }

//resize (crop) image
private void resize() { new WindowResize(mainFrame, theModel); }

//flip image
private void flip(boolean horizontally) {
	if (horizontally) theModel.flip(0);
	else theModel.flip(1);
}

//rotate image
private void rotate(Canvas.Transform option) { theModel.rotate(option); }

//temp
private void dummy() { } //temporary for menu listeners

}
