// Copyright 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.plastic;

import org.apache.tapestry5.internal.plastic.asm.tree.AnnotationNode;
import org.apache.tapestry5.plastic.AnnotationAccess;

import java.lang.annotation.Annotation;
import java.util.List;

class PlasticMember implements AnnotationAccess
{
    private final AnnotationAccess annotationAccess;

    protected final PlasticClassImpl plasticClass;

    PlasticMember(PlasticClassImpl plasticClass, List visibleAnnotations)
    {
        this.plasticClass = plasticClass;
        annotationAccess = plasticClass.pool.createAnnotationAccess((List<AnnotationNode>) visibleAnnotations);
    }

    public <T extends Annotation> boolean hasAnnotation(Class<T> annotationType)
    {
        plasticClass.check();

        return annotationAccess.hasAnnotation(annotationType);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType)
    {
        plasticClass.check();

        return annotationAccess.getAnnotation(annotationType);
    }

}
