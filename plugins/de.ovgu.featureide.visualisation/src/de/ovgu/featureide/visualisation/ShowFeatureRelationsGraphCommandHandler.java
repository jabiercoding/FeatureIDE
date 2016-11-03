package de.ovgu.featureide.visualisation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.ovgu.featureide.core.CorePlugin;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.fm.core.FeatureDependencies;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.ui.handlers.base.ASelectionHandler;

/**
 * Show Feature Relations Graph
 * 
 * @author jabier.martinez
 */
public class ShowFeatureRelationsGraphCommandHandler extends ASelectionHandler {

	@Override
	protected void singleAction(Object element) {
		IProject project = null;
		if (!(element instanceof IProject)) {
			if (element instanceof IAdaptable) {
				project = ((IAdaptable) element).getAdapter(IProject.class);
			}
		} else {
			project = (IProject) element;
		}
		final IFeatureProject featureProject = CorePlugin.getFeatureProject(project);
		Shell shell = new Shell(Display.getCurrent());
		shell.setText("Select feature");
		shell.setSize(400, 200);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		final org.eclipse.swt.widgets.List list = new org.eclipse.swt.widgets.List(shell, SWT.BORDER | SWT.V_SCROLL);

		List<String> featureList = ConfigAnalysisUtils.getNoCoreNoHiddenFeatures(featureProject);
		for (String f : featureList) {
			list.add(f);
		}

		list.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				int[] selections = list.getSelectionIndices();
				showFrog(featureProject, list.getItem(selections[0]));
			}

			public void widgetDefaultSelected(SelectionEvent event) {

			}
		});

		shell.open();

	}

	// P(i|currentI)
	public static double getGivenOperation(boolean[][] matrix, int currentI, int i) {
		double numerator = 0;
		double denominator = 0;
		for (int conf = 0; conf < matrix.length; conf++) {
			if (matrix[conf][currentI]) {
				denominator = denominator + 1.0;
				if (matrix[conf][i]) {
					numerator = numerator + 1.0;
				}
			}
		}
		if (denominator == 0) {
			return 0;
		}
		return numerator / denominator;
	}

	/**
	 * Show frog
	 * 
	 * @param featureProject
	 * @param featureCenter
	 */
	public static void showFrog(IFeatureProject featureProject, String featureCenter) {

		// Get feature in the center
		IFeature fc = featureProject.getFeatureModel().getFeature(featureCenter);

		// Get formalized constraints, implies and excludes
		List<String> formalizedRequires = new ArrayList<String>();
		List<String> formalizedExcludes = new ArrayList<String>();
		FeatureDependencies fd = new FeatureDependencies(featureProject.getFeatureModel());
		for (IFeature f : fd.always(fc)) {
			formalizedRequires.add(f.getName());
		}
		for (IFeature f : fd.never(fc)) {
			formalizedExcludes.add(f.getName());
		}

		// Get all features in order ignoring the mandatory features
		List<String> featureList = ConfigAnalysisUtils.getNoCoreNoHiddenFeatures(featureProject);
		// Create the matrix configurations/features for the calculations
		boolean[][] matrix = null;
		try {
			matrix = ConfigAnalysisUtils.getConfigsMatrix(featureProject, featureList);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		// Here we create the text with the data to be inserted in the html page

		// var CENTRAL_FEATURE = "Feature C";
		// var FEATURE_NAMES = ["Feature 1", "Feature 2", "Feature 3", "Feature 4", "Feature 5", "Feature 6"];
		// var GIVEN = [1, 0.99, 0.66, 0.5, 0.01, 0];
		// var FORMALIZED_REQUIRES = [];
		// var FORMALIZED_EXCLUDES = [];

		StringBuffer data = new StringBuffer(" CENTRAL_FEATURE = \"");
		data.append(featureCenter);
		data.append("\";\n FEATURE_NAMES = [");
		for (String f : featureList) {
			if (!f.equals(featureCenter)) {
				data.append("\"");
				data.append(f);
				data.append("\",");
			}
		}
		data.setLength(data.length() - 1); // remove last comma
		data.append("];\n GIVEN = [");
		for (String f : featureList) {
			if (!f.equals(featureCenter)) {
				int i = featureList.indexOf(f);
				int ic = featureList.indexOf(featureCenter);
				data.append(getGivenOperation(matrix, ic, i));
				data.append(",");
			}
		}
		data.setLength(data.length() - 1); // remove last comma
		boolean atLeastOne = false;
		data.append("];\n FORMALIZED_REQUIRES = [");
		for (String f : formalizedRequires) {
			data.append("\"");
			data.append(f);
			data.append("\",");
			atLeastOne = true;
		}
		if (atLeastOne) {
			data.setLength(data.length() - 1); // remove last comma
		}
		atLeastOne = false;
		data.append("];\n FORMALIZED_EXCLUDES = [");
		for (String f : formalizedExcludes) {
			data.append("\"");
			data.append(f);
			data.append("\",");
			atLeastOne = true;
		}
		if (atLeastOne) {
			data.setLength(data.length() - 1); // remove last comma
		}
		data.append("];\n");

		File fi = Utils.getFileFromPlugin("de.ovgu.featureide.visualisation", "template/featureRelations/page.html");
		String html = Utils.getStringOfFile(fi);
		html = html.replaceFirst("// DATA_HERE", data.toString());

		// Open the browser
		Shell shell = new Shell(Display.getCurrent());
		shell.setLayout(new FillLayout());
		shell.setSize(900, 800);
		shell.setText("Feature relations graph: " + featureCenter);
		Browser browser;
		try {
			browser = new Browser(shell, SWT.NONE);
		} catch (SWTError e) {
			System.out.println("Could not instantiate Browser: " + e.getMessage());
			return;
		}
		browser.setText(html);
		shell.open();
	}
}
