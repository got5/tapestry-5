// Copyright 2012, 2013 The Apache Software Foundation
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

package org.apache.tapestry5.internal.services.javascript;

import org.apache.tapestry5.internal.services.AssetDispatcher;
import org.apache.tapestry5.internal.services.ResourceStreamer;
import org.apache.tapestry5.ioc.IOOperation;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.PathConstructor;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.javascript.ModuleManager;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

/**
 * Handler contributed to {@link AssetDispatcher} with key "modules". It interprets the extra path as a module name,
 * and searches for the corresponding JavaScript module.  Unlike normal assets, modules do not include any kind of checksum
 * in the URL, and do not set a far-future expires header.
 *
 * @see ModuleManager
 */
public class ModuleDispatcher implements Dispatcher
{
    private final ModuleManager moduleManager;

    private final ResourceStreamer streamer;

    private final OperationTracker tracker;

    private final String requestPrefix;

    private final boolean compress;

    private final Set<ResourceStreamer.Options> omitExpiration = EnumSet.of(ResourceStreamer.Options.OMIT_EXPIRATION);

    public ModuleDispatcher(ModuleManager moduleManager,
                            ResourceStreamer streamer,
                            OperationTracker tracker,
                            PathConstructor pathConstructor,
                            String prefix,
                            boolean compress)
    {
        this.moduleManager = moduleManager;
        this.streamer = streamer;
        this.tracker = tracker;
        this.compress = compress;

        requestPrefix = pathConstructor.constructDispatchPath(compress ? prefix + ".gz" : prefix) + "/";
    }

    public boolean dispatch(Request request, Response response) throws IOException
    {
        String path = request.getPath();

        return path.startsWith(requestPrefix) &&
                handleModuleRequest(path.substring(requestPrefix.length()));

    }

    private boolean handleModuleRequest(String extraPath) throws IOException
    {
        // Ensure request ends with '.js'.  That's the extension tacked on by RequireJS because it expects there
        // to be a hierarchy of static JavaScript files here. In reality, we may be cross-compiling CoffeeScript to
        // JavaScript, or generating modules on-the-fly, or exposing arbitrary Resources from somewhere on the classpath
        // as a module.

        int dotx = extraPath.lastIndexOf('.');

        if (dotx < 0)
        {
            return false;
        }

        if (!extraPath.substring(dotx + 1).equals("js"))
        {
            return false;
        }

        final String moduleName = extraPath.substring(0, dotx);

        return tracker.perform(String.format("Streaming %s %s",
                compress ? "compressed module" : "module",
                moduleName), new IOOperation<Boolean>()
        {
            public Boolean perform() throws IOException
            {
                Resource resource = moduleManager.findResourceForModule(moduleName);

                if (resource != null)
                {
                    // Slightly hacky way of informing the streamer whether to supply the
                    // compressed or default stream. May need to iterate the API on this a bit.
                    return streamer.streamResource(resource, compress ? "z" : "", omitExpiration);
                }

                return false;
            }
        });
    }
}
