package org.nkjmlab.webui.jaxrs.msgpack;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Provider
@Produces("application/x-msgpack")
@Consumes("application/x-msgpack")
public class MessagePackProvider extends JacksonJsonProvider {
	public MessagePackProvider() {
		super(new ObjectMapper(new MessagePackFactory()));
	}

	@Override
	protected boolean hasMatchingMediaType(MediaType mediaType) {
		if (mediaType != null) {
			String subtype = mediaType.getSubtype();
			return "x-msgpack".equals(subtype);
		}
		return false;
	}
}
