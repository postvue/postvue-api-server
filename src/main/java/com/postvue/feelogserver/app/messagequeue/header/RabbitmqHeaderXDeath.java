package com.postvue.feelogserver.app.messagequeue.header;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitmqHeaderXDeath {
	private int count; // 메시지가 x-death 상태가 된 횟수
	private String exchange; // 관련된 교환기 이름
	private String queue; // 관련된 큐 이름
	private String reason; // 메시지가 DLQ로 들어간 이유
	private List<String> routingKeys; // 라우팅 키 리스트
	private Date time; // 메시지가 x-death 상태가 된 시간

	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		RabbitmqHeaderXDeath other = (RabbitmqHeaderXDeath) obj;

		if (count != other.count){
			return false;
		}

		if (exchange == null){
			return false;
		}
		else if (!exchange.equals((other.exchange))) {
			return false;
		}

		if (queue == null && other.queue != null){
			return false;
		}

		else if(!queue.equals((other.queue))){
			return false;
		}

		if (reason == null){
			if (other.reason != null){
				return false;
			}
		}
		else if (!reason.equals((other.reason))){
			return false;
		}

		if (routingKeys == null){
			if (other.routingKeys != null){
				return false;
			}
		}
		else if (!routingKeys.equals((other.routingKeys))){
			return false;
		}

		if (time == null){
			if (other.time != null){
				return false;
			}
		}
		else if (!time.equals((other.time))){
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 19;
		int result = 1;
		result = prime * result + count;
		result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
		result = prime * result + ((queue == null) ? 0 : queue.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		result = prime * result + ((routingKeys == null) ? 0 : routingKeys.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}
}
