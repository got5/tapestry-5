// Copyright 2010, 2012 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.apache.tapestry5.internal.beanvalidator;

import org.apache.tapestry5.beanvalidator.ClientConstraintDescriptor;
import org.apache.tapestry5.beanvalidator.ClientConstraintDescriptorSource;

import java.util.Collection;

import static org.apache.tapestry5.ioc.internal.util.CollectionFactory.newList;

public class ClientConstraintDescriptorImpl implements ClientConstraintDescriptorSource
{

    private Collection<ClientConstraintDescriptor> descriptors = newList();

    public ClientConstraintDescriptorImpl(
            final Collection<ClientConstraintDescriptor> configuration)
    {
        this.descriptors = configuration;
    }

    public ClientConstraintDescriptor getConstraintDescriptor(final Class annotationClass)
    {
        for (final ClientConstraintDescriptor desc : this.descriptors)
        {
            if (desc.getAnnotationClass().equals(annotationClass))
            {
                return desc;
            }
        }

        return null;
    }

}