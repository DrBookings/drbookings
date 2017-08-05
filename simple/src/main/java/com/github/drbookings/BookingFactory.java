/*******************************************************************************
 * Copyright (c) 2017 Alexander Kerner. All rights reserved.
 *
 * Licensed under the GNU General Public License, Version 2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.gnu.org/licenses/gpl-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.github.drbookings;

import java.util.Collection;

import com.github.drbookings.model.ser.BookingBeanSer;

/**
 *
 * @author Alexander Kerner
 *
 */
public interface BookingFactory {

	Collection<BookingBeanSer> build() throws Exception;

}
