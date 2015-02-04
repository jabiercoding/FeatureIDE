/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2013  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.fm.core.conversion;

import java.util.LinkedHashSet;
import java.util.Set;

import org.prop4j.Literal;
import org.prop4j.Node;
import org.prop4j.NodeReader;

import de.ovgu.featureide.fm.core.Constraint;
import de.ovgu.featureide.fm.core.FeatureModel;

/**
 * Class that converts any propositional formula into a product-equivalent 
 * feature model representing that formula.
 * 
 * Each atomic formula will have a concrete feature in the resulting model.
 * Additionally, the model will have intermediate abstract features.
 * 
 * This is a proof-of-concept, the resulting feature model is not optimized 
 * for any particular purpose. The conversion also does not try to preserve 
 * the structure of the formula in the result model.
 * 
 * @author Arthur Hammer
 */
public class FormulaConverter {
	
	public FeatureModel convert(String formula) {
		if (formula == null) {
			throw new IllegalArgumentException("Formula cannot be null.");
		}
		
		NodeReader reader = new NodeReader();
		return convert(reader.stringToNode(formula));
	}
	
	public FeatureModel convert(Node formula) {
		if (formula == null) {
			throw new IllegalArgumentException("Formula cannot be null.");
		}
		
		FeatureModel fm = new FeatureModel();
		ComplexConstraintConverter converter = new ComplexConstraintConverter();
		// converter.fm needs to be set before calling any of the converter's internal methods
		converter.fm = fm;
		
		Set<String> featureNames = getFeatureNames(formula);
		String rootName = "Root";
		
		int i = 1;
		while (featureNames.contains(rootName)) {
			rootName += "-" + (i++);
		}
		
		fm.setRoot(converter.createAbstractFeature(rootName, false, true));
		
		for (String name: featureNames) {
			converter.createFeatureUnderRoot(name, false);
		}
		
		fm.addConstraint(new Constraint(fm, formula));
		return converter.convert(fm);
	}
	
	private Set<String> getFeatureNames(Node node) {
		Set<String> features = new LinkedHashSet<String>();
		getFeatureNames(node, features);
		return features;
	}
	
	private void getFeatureNames(Node node, Set<String> features) {
		if (node instanceof Literal) {
			features.add(((Literal) node).var.toString());
		} 
		else {
			for (Node child : node.getChildren()){
				getFeatureNames(child, features);
			}
		}
	}
}
