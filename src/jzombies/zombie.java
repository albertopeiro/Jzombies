package jzombies;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.*;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.*;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;


public class zombie {

	// Declaración de variables donde estará el zombie: Se moverá por la variable Space de la clase continuous Space
	// Las variables tienen Object como parámetro template, lo que nos permitirá poner cualquier objeto en ellas?
		private ContinuousSpace<Object> space;
		private Grid<Object> grid;
		private boolean moved;
		
	// Constructor de la clase Zombie. Esta clase necesitará dos inputs para funcionar, el espacio y la red
		public zombie (ContinuousSpace<Object> space, Grid<Object>grid){
			
			this.space=space;
			this.grid=grid;
		}
		
	//This is to call the method step every iteration of the simulation
	//starting at timestep 1 and being called every interval of 1
		
@ ScheduledMethod (start=1,interval=1)

	// Método step perteneciente a la clase zombie
	// Método setter que no necesita parámetros de entrada
public void step(){
// 	Coge la ubicación de este Zombie
	// This dentro de getLocation hace referencia al objeto el método del cual está llamando al código
	GridPoint pt = grid.getLocation(this);
	
	// Usar la clase GridCellNgh para crear celdas para el "vecindario" alrededor
	// El vecindaro se refiere a las 8 celdas adyacentes a la celda actual
	// Este constructor new objecto de la clase GridCellNgh nos permite especificar el vecindario y la ubicación de un zombie
	// Poniendo human com objeto template filtra todo lo que no sean humanos para determinar la dirección del zombie
	GridCellNgh<human>nghCreator=new GridCellNgh<human> (grid, 
			pt, human.class,1,1);
	
	
	/* Esto es para crear un listado de gridCells conteniendo humanos
	 Lllamamos al metodo getNeighborhood, que con true devuelve 
	 la ubicación del zombie dentro del listado de celdas vecinas (8+1)*/
	
	/* Usamos el shuffle para barajar el listado de celdas. Esto es para que
	 el zombie se mueva aleatoriamente si todas las celdas son iguales y no
	 siempre en la misma dirección. La clase RandomHelper nos da un generador
	 aleatorio para barajar de forma aleatoria y no siguiendo un pattern*/
	
	
	List<GridCell<human>> gridCells=nghCreator.getNeighborhood(true);
	SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
	
	/*Definimos la variable pointWithMostHumans pero no le damos valor
	
	Tambien definimos la variable maxCount que cambiará con
	el bucle for each que recorre toda la lista gridCells creada anteriormente
	size es el número de objetos en la celda, que como hemos filtrado arriba, 
	solo seran humanos, tendra que ser mayor que maxCount, que pasará a tener
	el nuevo valor de cell size. Luego volverá a ser -1 con cada nuevo paso que
	tenga que dar el zombie
	
	En el caso de que la celda tenga mas humanos que las anteriores,
	pointWithMostHumans pasará a ser esa celda y maxCount su tamaño.
	*/
	GridPoint pointWithMostHumans = null;
	int maxCount=-1;
	for (GridCell<human> cell : gridCells){
		if (cell.size() > maxCount){
			pointWithMostHumans = cell.getPoint();
			maxCount=cell.size();
		}
	}
	moveTowards(pointWithMostHumans);
	infect();
}


public void moveTowards (GridPoint pt){

	// El zombie se moverá hacia pt
	// Solo muévete si no estas en la celda con más humanos
	if (!pt.equals(grid.getLocation(this))){
	// Creates an NdPoint (myPoint) where the Zombie is
		NdPoint myPoint = space.getLocation(this);
	// Convert the GridPoint pt to NdPoint
		NdPoint otherPoint= new NdPoint(pt.getX(), pt.getY());
	// Calculates the angle of the movement
		double angle=SpatialMath.calcAngleFor2DMovement(space, myPoint, otherPoint);
	// Sets the vector of movement
		space.moveByVector(this, 1, angle, 0);
	// Moves the object
		myPoint=space.getLocation(this);
		grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
	
	moved=true;
	}
}	
	public void infect(){
		GridPoint pt=grid.getLocation(this);
		List<Object> humans= new ArrayList<Object>();
		for (Object obj: grid.getObjectsAt (pt.getX(), pt.getY())) {
			if (obj instanceof human){
				humans.add(obj);
			}
			
		}
		if (humans.size()>0){
			int index=RandomHelper.nextIntFromTo(0, humans.size()-1);
			Object obj=humans.get(index);
			NdPoint spacePt =space.getLocation(obj);
			@SuppressWarnings("unchecked")
			Context<Object> context=ContextUtils.getContext(obj);
			context.remove(obj);
			zombie Zombie=new zombie(space,grid);
			context.add(Zombie);
			space.moveTo(Zombie,  spacePt.getX(), spacePt.getY());
			grid.moveTo(Zombie,pt.getX(), pt.getY());
			
			Network<Object> net= (Network<Object>)context.getProjection("Mushroom network");
			net.addEdge(this,Zombie);
		}


	}

}
