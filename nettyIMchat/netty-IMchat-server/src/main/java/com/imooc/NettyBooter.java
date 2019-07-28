/**  
* <p>Title: NettyBooter.java</p>   
* @author Luff
* @date 2019年4月13日  
*/  
package com.imooc;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.imooc.netty.WSServer;

@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent>{

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		if(event.getApplicationContext().getParent() == null){
			
			try {
				
				WSServer.getInstance().start();
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
	}

}
