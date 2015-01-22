package de.ovgu.featureide.core.cide;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

//import de.ovgu.featureide.ui.editors.annotation.ColorAnnotationModel;

public class MarkWithFeatureAction implements IEditorActionDelegate,
		IViewActionDelegate {
	
	ColorXmlManager colorXmlManager;
	SelectFeatureDialog selectFeatureDialog = new SelectFeatureDialog();

	public ITextEditor activeEditor = null;


	public void run(IAction action) {
	
		FileEditorInput input = (FileEditorInput)activeEditor.getEditorInput() ;
	    IFile file = input.getFile();
	    IProject activeProject = file.getProject();
	    String activeProjectPath = activeProject.getLocation().toFile().getAbsolutePath();
		System.out.println(activeProjectPath); 
		
		this.colorXmlManager = new ColorXmlManager(activeProjectPath);
		
		// Magic --> eintragen in XML
		String feature = selectFeatureDialog.open(activeEditor);
		
		this.colorXmlManager.addAnnotation();

		//ColorAnnotationManager colorAnnotationManager = new ColorAnnotationManager(); 
		ISelectionProvider selectionProvider = activeEditor .getSelectionProvider(); 
		ISelection selection = selectionProvider.getSelection();

	}


	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void init(IViewPart view) {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof ITextEditor) {
			activeEditor = (ITextEditor) targetEditor;
			System.out.println("setActiveEditor: "+activeEditor);
		}
	}
	
}
