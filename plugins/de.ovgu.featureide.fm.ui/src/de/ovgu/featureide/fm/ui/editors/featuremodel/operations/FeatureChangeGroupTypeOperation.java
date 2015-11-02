/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
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
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.ui.editors.featuremodel.operations;

import static de.ovgu.featureide.fm.core.localization.StringTable.CHANGE_GROUP_TYPE;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

/**
 * Operation with functionality to change group types. Enables undo/redo
 * functionality.
 * 
 * @author Fabian Benduhn
 */
public class FeatureChangeGroupTypeOperation extends AbstractFeatureModelOperation {

	public static final int ALTERNATIVE = 0;
	public static final int AND = 1;
	public static final int OR = 2;

	protected IFeature feature;
	private int groupType;
	private int oldGroupType;

	/**
	 * Grouptype of feature will be set to groupType when this operation is
	 * executed
	 */
	public FeatureChangeGroupTypeOperation(int groupType, IFeature feature, IFeatureModel featureModel) {
		super(featureModel, CHANGE_GROUP_TYPE);
		this.groupType = groupType;
		this.oldGroupType = getGroupType(feature);
		this.feature = feature;
	}

	@Override
	protected void redo() {
		if (groupType == ALTERNATIVE) {
			feature.getStructure().changeToAlternative();
		} else if (groupType == OR) {
			feature.getStructure().changeToOr();
		} else {
			feature.getStructure().changeToAnd();
		}
	}

	@Override
	protected void undo() {
		if (oldGroupType == ALTERNATIVE) {
			feature.getStructure().changeToAlternative();
		} else if (oldGroupType == AND) {
			feature.getStructure().changeToAnd();
		} else {
			feature.getStructure().changeToOr();
		}
	}

	private static int getGroupType(IFeature feature) {
		if (feature.getStructure().isAlternative()) {
			return ALTERNATIVE;
		} else if (feature.getStructure().isAnd()) {
			return AND;
		} else {
			return OR;
		}
	}

}
