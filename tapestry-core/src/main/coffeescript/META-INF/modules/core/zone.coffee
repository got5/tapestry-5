# Copyright 2012 The Apache Software Foundation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http:#www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# ##core/zone
#
# Provides a default handler for the `events.zone.update` event, attached to the
# document body.
define ["core/spi", "core/events"],
  (spi, events) ->
    spi.domReady ->
      spi.body().on events.zone.update, (event) ->
        zone = spi.wrap this

        zone.trigger events.zone.willUpdate

        # TODO: purge existing children?

        zone.update event.memo

        zone.show() unless zone.visible()

        zone.trigger events.zone.didUpdate

    # No meaningful value is returned.
    true