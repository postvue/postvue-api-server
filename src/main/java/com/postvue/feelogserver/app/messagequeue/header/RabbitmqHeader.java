package com.postvue.feelogserver.app.messagequeue.header;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.postvue.feelogserver.global.constant.RabbitMQConst;

public class RabbitmqHeader {
	private List<RabbitmqHeaderXDeath> xDeaths = new ArrayList<>(2);
	private String xFirstDeathExchange = StringUtils.EMPTY;
	private String xFirstDeathQueue = StringUtils.EMPTY;
	private String xFirstDeathReason = StringUtils.EMPTY;

	@SuppressWarnings("unchecked")
	public RabbitmqHeader(Map<String, Object> headers){
		if (headers != null){
			var xFirstDeathExchange = Optional.ofNullable(headers.get("x-first-death-exchange"));
			var xFirstDeathQueue = Optional.ofNullable(headers.get("x-first-death-queue"));
			var xFirstDeathReason = Optional.ofNullable(headers.get("x-first-death-reason"));

			xFirstDeathExchange.ifPresent(s -> this.setXFirstDeathExchange(s.toString()));
			xFirstDeathQueue.ifPresent(s -> this.setxFirstDeathQueue(s.toString()));
			xFirstDeathReason.ifPresent(s -> this.setxFirstDeathReason(s.toString()));

			var xDeathHeaders = (List<Map<String, Object>>) headers.get("x-death");

			if (xDeathHeaders != null) {
				for (Map<String, Object> x : xDeathHeaders) {
					RabbitmqHeaderXDeath rabbitmqHeaderXDeath = new RabbitmqHeaderXDeath();
					var reason = Optional.ofNullable(x.get("reason"));
					var count = Optional.ofNullable(x.get("count"));
					var exchange = Optional.ofNullable(x.get("exchange"));
					var queue = Optional.ofNullable(x.get("queue"));
					var routingKeys = Optional.ofNullable(x.get("routing-keys"));
					var time = Optional.ofNullable(x.get("time"));

					reason.ifPresent(s -> rabbitmqHeaderXDeath.setReason(s.toString()));
					count.ifPresent(s -> rabbitmqHeaderXDeath.setCount(Integer.parseInt(s.toString())));
					exchange.ifPresent(s -> rabbitmqHeaderXDeath.setExchange(s.toString()));
					queue.ifPresent(s -> rabbitmqHeaderXDeath.setQueue(s.toString()));
					routingKeys.ifPresent(r -> {
						var listR = (List<String>) r;
						rabbitmqHeaderXDeath.setRoutingKeys(listR);
					});
					time.ifPresent(d -> rabbitmqHeaderXDeath.setTime((Date) d));

					xDeaths.add(rabbitmqHeaderXDeath);
				}
			}
		}
	}

	public int getFailedRetryCount() {
		// get from queue "wait"
		for (var xDeath : xDeaths) {
			if (xDeath.getExchange().toLowerCase().endsWith(RabbitMQConst.WORK_SUFFIX)
				&& xDeath.getQueue().toLowerCase().endsWith(RabbitMQConst.WORK_SUFFIX)) {
				return xDeath.getCount();
			}
		}
		return 0;
	}


	public List<RabbitmqHeaderXDeath> getXDeaths() {
		return xDeaths;
	}

	public String getXFirstDeathExchange() {
		return xFirstDeathExchange;
	}

	public String getXFirstDeathQueue() {
		return xFirstDeathQueue;
	}

	public String getXFirstDeathReason() {
		return xFirstDeathReason;
	}

	public void setXDeaths(List<RabbitmqHeaderXDeath> xDeaths) {
		this.xDeaths = xDeaths;
	}

	public void setXFirstDeathExchange(String xFirstDeathExchange) {
		this.xFirstDeathExchange = xFirstDeathExchange;
	}

	public void setxFirstDeathQueue(String xFirstDeathQueue) {
		this.xFirstDeathQueue = xFirstDeathQueue;
	}

	public void setxFirstDeathReason(String xFirstDeathReason) {
		this.xFirstDeathReason = xFirstDeathReason;
	}
}
