package it.unisannio.aroundme.client.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import it.unisannio.aroundme.model.DataListener;
import it.unisannio.aroundme.model.SerializerUtils;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class XmlClient implements Client<Node> {
	private static class BytesToNodeListener implements DataListener<byte[]> {
		private DataListener<Node> nodeListener;
		
		BytesToNodeListener(DataListener<Node> nodeListener) {
			this.nodeListener = nodeListener;
		}
		
		@Override
		public void onData(byte[] object) {
			try {
				DocumentBuilder b = SerializerUtils.getDocumentBuilder();
				b.parse(new InputSource(new ByteArrayInputStream(object)));
			} catch (Exception e) {
				onError(e);
			}
		}

		@Override
		public void onError(Exception e) {
			nodeListener.onError(e);
		}
		
	};
	
	private RawClient service;
	
	public XmlClient(String endpoint) {
		service = new RawClient(endpoint);
	}
	
	@Override
	public void get(String path, DataListener<Node> listener) {
		service.get(path, new BytesToNodeListener(listener));
	}

	@Override
	public void put(String path, Node data, DataListener<Node> listener) {
		try {
			service.put(path, nodeToBytes(data), new BytesToNodeListener(listener));
		} catch (Exception e) {
			listener.onError(e);
		}
	}

	@Override
	public void post(String path, Node data, DataListener<Node> listener) {
		try {
			service.post(path, nodeToBytes(data), new BytesToNodeListener(listener));
		} catch (Exception e) {
			listener.onError(e);
		}
	}

	@Override
	public void delete(String path, DataListener<Node> listener) {
		service.delete(path,  new BytesToNodeListener(listener));
	}

	private byte[] nodeToBytes(Node node) throws Exception {
		Source source = new DOMSource(node);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = new StreamResult(out);

        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, result);
        
        return out.toByteArray();
	}
}
