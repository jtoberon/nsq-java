package ly.bit.nsq.lookupd;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import ly.bit.nsq.NSQReader;


public abstract class AbstractLookupd {
	
	protected String addr;

	/**
	 * This should handle making a request to lookupd, and returning which producers match the channel we want
	 * Netty presumably can wait on the future or something, who knows...
	 */
	public abstract List<String> query(String topic);

	public static List<String> parseResponseForProducers(Reader response){
		ObjectMapper mapper = new ObjectMapper();
		List<String> outputs = new ArrayList<String>();
		try {	 
			 JsonNode rootNode = mapper.readTree(response);
			 JsonNode producers = rootNode.path("data").path("producers");
			 Iterator<JsonNode> prodItr = producers.getElements();
			 while(prodItr.hasNext()){
				 JsonNode producer = prodItr.next();
				 String addr = producer.path("address").getTextValue();
				 int tcpPort = producer.path("tcp_port").getIntValue();
				 outputs.add(addr + ":" + tcpPort);
			 }
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputs;
	}
	
	public static void main(String... args){
		String response = "{\"status_code\":200,\"status_txt\":\"OK\",\"data\":{\"channels\":[\"social_graph_input\"],\"producers\":[{\"address\":\"dev.bitly.org\",\"tcp_port\":4150,\"http_port\":4151,\"version\":\"0.2.16-alpha\"}]}}";
//		for (String addr : parseResponseForProducers(response)){
//			System.out.println(addr);
//		}
	}
	
}
