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

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.category.CategoryResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.util.Assert;

import java.util.Collection;
import java.util.LinkedList;

import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;
import static java.util.Arrays.asList;

@SuppressWarnings("OverlyComplexAnonymousInnerClass")
public class InclusionService implements InclusionConfigurer, IsIgnoredResolver
{
	private final ObjectDifferBuilder rootConfiguration;
	private final Collection<InclusionResolver> inclusionResolvers;
	private final TypeInclusionResolver typeInclusionResolver;
	private final TypePropertyConfigInclusionResolver typePropertyConfigInclusionResolver;
	private final CategoryInclusionResolver categoryInclusionResolver;
	private final NodePathInclusionResolver nodePathInclusionResolver;
	private final PropertyNameInclusionResolver propertyNameInclusionResolver;

	public InclusionService(final CategoryResolver categoryResolver, final ObjectDifferBuilder rootConfiguration)
	{
		Assert.notNull(rootConfiguration, "rootConfiguration");
		this.rootConfiguration = rootConfiguration;
		this.inclusionResolvers = new LinkedList<InclusionResolver>(asList(
				new TypePropertyAnnotationInclusionResolver(),
				typePropertyConfigInclusionResolver = new TypePropertyConfigInclusionResolver(),
				categoryInclusionResolver = new CategoryInclusionResolver(categoryResolver),
				typeInclusionResolver = new TypeInclusionResolver(),
				nodePathInclusionResolver = new NodePathInclusionResolver(),
				propertyNameInclusionResolver = new PropertyNameInclusionResolver()
		));
	}

	public boolean isIgnored(final DiffNode node)
	{
		if (node.isRootNode())
		{
			return false;
		}
		boolean strictIncludeModeEnabled = false;
		boolean isExplicitlyIncluded = false;
		for (final InclusionResolver inclusionResolver : inclusionResolvers)
		{
			if (inclusionResolver.enablesStrictIncludeMode())
			{
				strictIncludeModeEnabled = true;
			}
			switch (inclusionResolver.getInclusion(node))
			{
				case EXCLUDED:
					return true;
				case INCLUDED:
					isExplicitlyIncluded = true;
					break;
			}
		}
		if (strictIncludeModeEnabled && !isExplicitlyIncluded)
		{
			return true;
		}
		return false;
	}

	public ToInclude include()
	{
		return new ToInclude()
		{
			public ToInclude category(final String category)
			{
				categoryInclusionResolver.setInclusion(category, INCLUDED);
				return this;
			}

			public ToInclude type(final Class<?> type)
			{
				typeInclusionResolver.setInclusion(type, INCLUDED);
				return this;
			}

			public ToInclude node(final NodePath nodePath)
			{
				nodePathInclusionResolver.setInclusion(nodePath, INCLUDED);
				return this;
			}

			public ToInclude propertyName(final String propertyName)
			{
				propertyNameInclusionResolver.setInclusion(propertyName, INCLUDED);
				return this;
			}

			public ToInclude propertyNameOfType(final Class<?> type, final String... propertyNames)
			{
				setPropertyNameOfTypeInclusion(INCLUDED, type, propertyNames);
				return this;
			}

			public InclusionConfigurer also()
			{
				return InclusionService.this;
			}

			public ObjectDifferBuilder and()
			{
				return rootConfiguration;
			}
		};
	}

	public ToExclude exclude()
	{
		return new ToExclude()
		{
			public ToExclude category(final String category)
			{
				categoryInclusionResolver.setInclusion(category, EXCLUDED);
				return this;
			}

			public ToExclude type(final Class<?> type)
			{
				typeInclusionResolver.setInclusion(type, EXCLUDED);
				return this;
			}

			public ToExclude node(final NodePath nodePath)
			{
				nodePathInclusionResolver.setInclusion(nodePath, EXCLUDED);
				return this;
			}

			public ToExclude propertyName(final String propertyName)
			{
				propertyNameInclusionResolver.setInclusion(propertyName, EXCLUDED);
				return this;
			}

			public ToExclude propertyNameOfType(final Class<?> type, final String... propertyNames)
			{
				setPropertyNameOfTypeInclusion(EXCLUDED, type, propertyNames);
				return this;
			}

			public InclusionConfigurer also()
			{
				return InclusionService.this;
			}

			public ObjectDifferBuilder and()
			{
				return rootConfiguration;
			}
		};
	}

	public InclusionConfigurer resolveUsing(final InclusionResolver inclusionResolver)
	{
		Assert.notNull(inclusionResolver, "inclusionResolver");
		inclusionResolvers.add(inclusionResolver);
		return this;
	}

	public ObjectDifferBuilder and()
	{
		return rootConfiguration;
	}

	private void setPropertyNameOfTypeInclusion(final Inclusion inclusion, final Class<?> type, final String... propertyNames)
	{
		Assert.notNull(type, "type");
		for (final String propertyName : propertyNames)
		{
			Assert.hasText(propertyName, "propertyName in propertyNames");
			typePropertyConfigInclusionResolver.setInclusion(type, propertyName, inclusion);
		}
	}
}