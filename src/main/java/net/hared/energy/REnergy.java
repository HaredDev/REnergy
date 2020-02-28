package net.hared.energy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.hared.energy.util.RE;
import net.hared.energy.util.REType;

/**
 * Main mod class
 * @author HARED
 *
 */
public class REnergy implements ModInitializer{
	
	private static REnergy instance;
	
	private final Logger log = LogManager.getLogger("REnergy");
	private final REnergy rEnergy;
	
	public REnergy() {
		log.info("[REenergy]Initializing!");
		this.rEnergy = this;
	}

	/**
	 * @return REnergy obj
	 */
	public static REnergy getInstance() {
		return instance;
	}
	
	/**
	 * 
	 * @param REEnergyModule class
	 * @return If Module is valid
	 */
	public boolean isREnergyModuleValid(Class<?> cl) {
		if(cl.isAnnotationPresent(RE.class) && ((RE)cl.getAnnotation(RE.class)).type() != null) {
			boolean hasRecive = false;
			boolean hasProduce = false;
			for(Method m : cl.getClass().getMethods()) {
				if(m.isAnnotationPresent(RE.Consumer.class)) {
					for(Class<?> c : m.getParameterTypes()) {
						if(m.getParameterTypes().length < 1 || c != float.class || c != Float.class)
							return false;
					}
					hasRecive = true;
				}
				if(m.isAnnotationPresent(RE.Producer.class)) {
					for(Class<?> c : m.getParameterTypes()) {
						if(m.getParameterTypes().length < 1 || c != float.class || c != Float.class)
							return false;
					}
					hasProduce = true;
				}
			}
			if(((RE)cl.getAnnotation(RE.class)).type() == REType.Consumer && hasRecive) {
				return true;
			}
			if(((RE)cl.getAnnotation(RE.class)).type() == REType.Producer && hasProduce) {
				return true;
			}
			if(((RE)cl.getAnnotation(RE.class)).type() == REType.Storage && hasRecive && hasProduce) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Transfer energy from one module to another
	 * @return Total amount of energy that was rejected from consumer and producer.
	 */
	public float tranferEnergy(Object producer, Object consumer, float energy){
		if(!this.isREnergyModuleValid(producer.getClass()) || !this.isREnergyModuleValid(consumer.getClass()))
			return energy;
		
		if(((RE)producer.getClass().getAnnotation(RE.class)).type() != REType.Consumer || ((RE)consumer.getClass().getAnnotation(RE.class)).type() != REType.Producer)
			return energy;
		
		Method mProducer = null;
		Method mConsumer = null;
		
		for(Method m : producer.getClass().getMethods()) {
			if(m.isAnnotationPresent(RE.Producer.class)) {
				mProducer = m;
				break;
			}
		}
		
		for(Method m : consumer.getClass().getMethods()) {
			if(m.isAnnotationPresent(RE.Consumer.class)) {
					mConsumer = m;
					break;
			}
		}
		
		try {
			if(mConsumer != null || mProducer != null) {
				float maxTransfer = ((RE.Producer)mProducer.getAnnotation(RE.Producer.class)).maxTransfer() > ((RE.Consumer)mConsumer.getAnnotation(RE.Consumer.class)).maxTransfer() ? ((RE.Consumer)mConsumer.getAnnotation(RE.Consumer.class)).maxTransfer() : ((RE.Producer)mProducer.getAnnotation(RE.Producer.class)).maxTransfer();
				final float energyFinal = maxTransfer > energy ? energy : maxTransfer;
				float energyBase = energyFinal;
				float energyLeft = energy - energyBase;
				energyBase =- (float) mProducer.invoke(producer, new Object[] {energyBase});
				energyLeft = energyFinal != energyBase ? energyLeft + (energyFinal-energyBase) : energyLeft;
				energyLeft =+ (float) mConsumer.invoke(consumer, new Object[] {energyBase});
				return energyLeft;
			}
		} catch(Exception e) {
			log.error("[REenergy]Something went wrong while transferring energy!: " + e.getMessage());
			log.catching(e);
		}
		
		return energy;
	}
	
	/**
	 * Retrieves data from module, if there is any
	 * @return data, null if there are no data
	 */
	public Object getData(Object module) {
		if(!this.isREnergyModuleValid(module.getClass()))
			return null;
		
		Object data = null;
		
		try {
			for(Field f : module.getClass().getFields()) {
				if(f.isAnnotationPresent(RE.Data.class)) {
					data = f.get(module);
					break;
				}
			}
			
			for(Method m : module.getClass().getMethods()) {
				if(m.isAnnotationPresent(RE.Data.class)) {
					data = data != null ? null : m.invoke(module, new Object[] {});
					break;
				}
			}
		}catch(Exception e) {
			log.error("[REenergy]Something went wrong while getting data!: " + e.getMessage());
			log.catching(e);
		}
		
		return data;
	}
	
	@Override
	public void onInitialize() {
		log.info("[REenergy]Initialized!");
		instance = this.rEnergy;
	}

}
