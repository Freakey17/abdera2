/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera2.ext.media;

import org.apache.abdera2.common.anno.Namespace;
import org.apache.abdera2.factory.AbstractExtensionFactory;
import org.apache.abdera2.factory.AbstractExtensionFactory.Impls;
import org.apache.abdera2.factory.AbstractExtensionFactory.Impl;

@SuppressWarnings("deprecation")
@Namespace(MediaConstants.MEDIA_NS)
@Impls(
  {
    @Impl(MediaAdult.class),
    @Impl(MediaCategory.class),
    @Impl(MediaContent.class),
    @Impl(MediaCopyright.class),
    @Impl(MediaCredit.class),
    @Impl(MediaDescription.class),
    @Impl(MediaGroup.class),
    @Impl(MediaHash.class),
    @Impl(MediaKeywords.class),
    @Impl(MediaPlayer.class),
    @Impl(MediaRating.class),
    @Impl(MediaRestriction.class),
    @Impl(MediaText.class),
    @Impl(MediaThumbnail.class),
    @Impl(MediaTitle.class)
  }
)
public final class MediaExtensionFactory 
  extends AbstractExtensionFactory implements MediaConstants {

}
