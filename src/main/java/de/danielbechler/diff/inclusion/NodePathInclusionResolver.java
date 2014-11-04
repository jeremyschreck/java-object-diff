/*
 * Copyright 2014 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.inclusion;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;

import static de.danielbechler.diff.inclusion.Inclusion.DEFAULT;
import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;

class NodePathInclusionResolver implements InclusionResolver
{
	private final InclusionNode nodeInclusions = new InclusionNode();
	private boolean containsIncluded;
	private boolean containsExcluded;

	public Inclusion getInclusion(final DiffNode node)
	{
		if (isInactive())
		{
			return DEFAULT;
		}
		final NodePath nodePath = node.getPath();
		final InclusionNode inclusionNode = nodeInclusions.getNodeForPath(nodePath);
		if (inclusionNode.isIncluded())
		{
			return INCLUDED;
		}
		if (inclusionNode.isExcluded())
		{
			return EXCLUDED;
		}
		return DEFAULT;
	}

	private boolean isInactive()
	{
		return !containsIncluded && !containsExcluded;
	}

	public boolean enablesStrictIncludeMode()
	{
		return containsIncluded;
	}

	public void setInclusion(final NodePath nodePath, final Inclusion inclusion)
	{
		nodeInclusions.getNodeForPath(nodePath).setValue(inclusion);
		containsIncluded = nodeInclusions.containsValue(INCLUDED);
		containsExcluded = nodeInclusions.containsValue(EXCLUDED);
	}
}
