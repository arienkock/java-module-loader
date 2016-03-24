package nl.positor.module.definition;

public interface Wireable {
	<T> void wire(T module);
}
