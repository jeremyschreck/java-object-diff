/*
 * Copyright 2013 Daniel Bechler
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

package de.danielbechler.diff.issues.issue77

import de.danielbechler.diff.ObjectDifferBuilder
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
class Issue77IT extends Specification {
	def "NullPointerException on null unsafe Comparable implementations"() {
		expect:
		  ObjectDifferBuilder.buildDefault().compare(new SomeObject(BigDecimal.ONE), new SomeObject(null));
	}

	class SomeObject {
		BigDecimal value;

		SomeObject(BigDecimal value) {
			this.value = value;
		}

		BigDecimal getValue() {
			return value
		}

		void setValue(final BigDecimal value) {
			this.value = value
		}
	}
}