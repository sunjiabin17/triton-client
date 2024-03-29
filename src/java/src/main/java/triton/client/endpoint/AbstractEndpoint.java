// Copyright (c) 2021, NVIDIA CORPORATION & AFFILIATES. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
//  * Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
//  * Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
//  * Neither the name of NVIDIA CORPORATION nor the names of its
//    contributors may be used to endorse or promote products derived
//    from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
// PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
// OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package triton.client.endpoint;

import com.google.common.base.Preconditions;
import java.util.Objects;
import triton.client.InferenceServerClient;
import triton.client.Util;

/**
 * Endpoint is an abstraction that allow different kinds of strategy to provide
 * ip and port for
 * {@link InferenceServerClient} to send requests.
 */
public abstract class AbstractEndpoint {
  private static final int RETRY_COUNT = 10;
  private String lastResult = "";

  abstract String getEndpointImpl() throws Exception;

  abstract int getEndpointNum() throws Exception;

  /**
   * Get string in ip:port[/path] format.
   *
   * @return
   * @throws Exception
   */
  public String getEndpoint() throws Exception
  {
    for (int i = 0; i < RETRY_COUNT; i++) {
      String url = this.getEndpointImpl();
      Preconditions.checkState(
          !Util.isEmpty(url),
          "getEndpointImpl should not return null or empty string!");
      if (!Objects.equals(this.lastResult, url) || this.getEndpointNum() < 2) {
        this.lastResult = url;
        return url;
      }
    }
    throw new RuntimeException(String.format(
        "Failed to get endpoint address after trying %d times.", RETRY_COUNT));
  }
}
