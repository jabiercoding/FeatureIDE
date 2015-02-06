package de.ovgu.featureide.core.cide;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;

public class SelectMarkedFeatureDialog {

	public String open(ITextEditor activeEditor, String path, Document doc) {

		ISelectionProvider selectionProvider = activeEditor.getSelectionProvider();
		ISelection selection = selectionProvider.getSelection();
		ITextSelection textSelection = (ITextSelection) selection;

		Integer startLine = Integer.valueOf(textSelection.getStartLine() + 1);
		Integer endLine = Integer.valueOf(textSelection.getEndLine() + 1);

		Shell parentShel = null;
		ListDialog listDialog = new ListDialog(parentShel);
		listDialog.setTitle("FeatureDialog");
		listDialog.setMessage("Choose feature");
		listDialog.setContentProvider(ArrayContentProvider.getInstance());
		listDialog.setLabelProvider(new LabelProvider());

		Vector<String> featureList = new Vector<String>();
		IFeatureProject featureProject = null;

		if (activeEditor != null) {
			IFile inputFile = ((FileEditorInput) activeEditor.getEditorInput()).getFile();
			featureProject = CorePlugin.getFeatureProject(inputFile);
		}
		if (featureProject != null) {

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			try {
				String linePathXPath = "root/files/file[@path='" + path + "']/feature/line";
				XPathExpression expression = xpath.compile(linePathXPath);
				NodeList lineNodes = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);

				for (int i = 0; i < lineNodes.getLength(); i++) {
					Node lineNode = lineNodes.item(i);
					Integer endlineAttribute = Integer.parseInt(lineNode.getAttributes().item(0).getNodeValue());
					Integer startlineAttribute = Integer.parseInt(lineNode.getAttributes().item(1).getNodeValue());
					
					if (endlineAttribute >= endLine && startlineAttribute <= startLine) {
						
						//get feature id and add to featurelist
						String feature = lineNode.getParentNode().getAttributes().item(0).getTextContent();
						featureList.add(feature);
					}
				}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//sort featurelist
		Collections.sort(featureList);

		// TODO sort FeatureList
		listDialog.setInput(featureList);
		if (listDialog.open() == Dialog.OK) {
			System.out.println("Selected feature: " + Arrays.toString(listDialog.getResult()));
			Object array[] = listDialog.getResult();
			return (String) array[0];
		}
		return null;

	}

}
