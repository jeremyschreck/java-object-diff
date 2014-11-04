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

package de.danielbechler.diff.inclusion

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification

class ExclusiveInclusionIT extends Specification {

	def 'type based property inclusion does NOT exclude nodes that are not explicitly included'() {
		given:
		  def base = new A(b: new B(foo: 'base', bar: 'base'), c: new C(baz: 'base'))
		  def working = new A(b: new B(foo: 'working', bar: 'base'), c: new C(baz: 'working'))

		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .filtering()
				  .returnNodesWithState(DiffNode.State.IGNORED).and()
				  .inclusion()
				  .include().propertyNameOfType(B, 'foo').and()
				  .build()
				  .compare(working, base)

		then:
		  node.getChild('b').getChild('foo').isChanged()
		  node.getChild('b').getChild('bar').isIgnored()
		  node.getChild('c').getChild('baz').isChanged()
	}

	class A {
		B b
		C c
	}

	class B {
		String foo
		String bar
	}

	class C {
		String baz
	}

	def 'property name inclusion does exclude nodes that are not explicitly included'() {
		given:
		  def base = new A(b: new B(foo: 'base', bar: 'base'), c: new C(baz: 'base'))
		  def working = new A(b: new B(foo: 'working', bar: 'working'), c: new C(baz: 'working'))

		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .filtering()
				  .returnNodesWithState(DiffNode.State.IGNORED).and()
				  .inclusion()
				  .include().propertyName('b').and()
				  .build()
				  .compare(working, base)

		then:
		  node.getChild('b').hasChanges()
		  node.getChild('b').getChild('foo').isChanged()
		  node.getChild('b').getChild('bar').isChanged()
		  node.getChild('c').isIgnored()
	}

	def 'node path inclusion does exclude nodes that are not explicitly included'() {}

	def 'type inclusion does exclude nodes that are not explicitly included'() {}

	def 'category inclusion does exclude nodes that are not explicitly included'() {}
}
