// ### WORLD OF CELLS ###
// created by nicolas.bredeche(at)upmc.fr
// date of creation: 2013-1-12

package cellularautomata;

import applications.simpleworld.AgentList;
import applications.simpleworld.Agent;
import applications.simpleworld.Animal;
import applications.simpleworld.Plant;
import applications.simpleworld.PredaAgent;
import applications.simpleworld.ProieAgent;
import cellularautomata.LsCell;
import worlds.World;

public class CellularAutomataAgents extends CellularAutomata {

	protected AgentList Buffer0[][];
	protected AgentList Buffer1[][];
	
	int births;
	
	World world;
	
	int deaths;

	public CellularAutomataAgents ( int __dx , int __dy, boolean __buffering, World _world )
	{
		super(__dx,__dy,__buffering);
		
		this.world = _world;

		Buffer0 = new AgentList[_dx][_dy];
		Buffer1 = new AgentList[_dx][_dy];

		for ( int x = 0 ; x != _dx ; x++ )
			for ( int y = 0 ; y != _dy ; y++ )
			{
				Buffer0[x][y] = new AgentList();
				Buffer1[x][y] = new AgentList();
			}

	}

	public void updateAgent(Agent a, int i, int j)
	{
		checkBounds ( i , j );
		/*
		if (this.world.getCellHeight(i, j) <= 0)
		{
			return;
		}
		/**/
		

		// Buffer0 is new buffer

		Buffer1[i][j].add(a);
		Buffer1[a.getCoordinate()[0]][a.getCoordinate()[1]].remove(a);
		//System.out.println("Nouveau Agent sur buffer 1 en [" + i + ", " + j + "]!");

		//System.out.println("On retire l'agent Agent sur buffer 1 en [" + a.getCoordinate()[0] + ", " + a.getCoordinate()[1] + "]!");

		////System.out.println(this.world.getAgents()[(this.x + dx + myAgents_lenX) % myAgents_lenX][(this.y + dy + myAgents_lenY) % myAgents_lenY].contains(this));

		a.setCoordinate(i, j);



	}
	/**/
	public void updateAgentInitially(Agent a, int i, int j)
	{
		checkBounds ( i , j );
		/**
		if (this.world.getCellHeight(i, j) <= 0)
		{
			return;
		}
		/**/
		births += 1;

		// Buffer0 is new buffer

		Buffer1[i][j].add(a);
		//System.out.println("Nouveau Agent sur buffer 1 en [" + i + ", " + j + "]!");


		////System.out.println(this.world.getAgents()[(this.x + dx + myAgents_lenX) % myAgents_lenX][(this.y + dy + myAgents_lenY) % myAgents_lenY].contains(this));

		a.setCoordinate(i, j);


	}
	/**/


	public AgentList getAgentList ( int __x, int __y )
	{
		checkBounds (__x,__y);

		AgentList value;

		if ( buffering == false )
		{
			value = Buffer0[__x][__y];
		}
		else
		{
			if ( activeIndex == 0 ) // read old buffer
			{
				value = Buffer0[__x][__y];
			}
			else
			{
				value = Buffer1[__x][__y];
			}
		}

		return value;
	}

	public AgentList getNewAgentList ( int __x, int __y )
	{
		checkBounds (__x,__y);

		AgentList value;

		if ( buffering == false )
		{
			value = Buffer0[__x][__y];
		}
		else
		{
			if ( activeIndex == 1 ) // read new buffer
			{
				value = Buffer0[__x][__y];
			}
			else
			{
				value = Buffer1[__x][__y];
			}
		}

		return value;
	}
	/*
	public void setParam(int __x, int __y, int __param)
	{
		checkBounds (__x,__y);

		if ( buffering == false )
		{
			Buffer0[__x][__y].parameter = __param;
		}
		else
		{
			if ( activeIndex == 0 ) // write new buffer
			{
				Buffer0[__x][__y].parameter = __param;
			}
			else
			{
				Buffer1[__x][__y].parameter = __param;
			}
		}
	}
/**/

	public AgentList[][] getCurrentBuffer()
	{

		return Buffer0;

	}
	public void step()
	{
		stepinit();

		for (int i = 0 ; i < this.getCurrentBuffer().length ; i++)
		{
			for (int j = 0 ; j < this.getCurrentBuffer()[i].length ; j++)
			{
				for ( Agent a : this.getCurrentBuffer()[i][j])
				{
					a.step();
				}

			}
		}
		stepfinalize();

	}
	public void stepinit()
	{
		births = 0;
		deaths = 0;
		for ( int x = 0 ; x != _dx ; x++ )
			for ( int y = 0 ; y != _dy ; y++ )
			{
				Buffer1[x][y] = (AgentList) Buffer0[x][y].clone();

			}
		//System.out.println("buffer1 devient buffer0 !");



	}

	public void stepfinalize()
	{

		for ( int x = 0 ; x != _dx ; x++ )
			for ( int y = 0 ; y != _dy ; y++ )
			{
				/**/
				for (Agent a : Buffer0[x][y])
				{/**/
					if (a instanceof Animal)
					{/**/
						Animal ani = (Animal)a;
						/**/
						int statet = ani.getState();
						/**/
						//System.out.println(ani.toString());
						if (statet == Animal.ALIVE)
						{

						}
						if (statet == Animal.DEAD)
						{
							if (ani instanceof ProieAgent)
							{
								ProieAgent.nbIndividusProie -= 1;
							}
							
							if (ani instanceof PredaAgent)
							{
								PredaAgent.nbIndividusPreda -= 1;
							}
							//System.out.println("On retire un mort");
							Buffer1[x][y].remove(ani);
							deaths += 1;
						}
						/**/
					}
					/**/


					if (a instanceof Plant)
					{/**/
						Plant ani = (Plant)a;
						/**/
						int statet = ani.getState();
						/**/
						//System.out.println(ani.toString());
						if (statet == Plant.ALIVE)
						{

						}
						if (statet == Plant.DEAD)
						{
							//System.out.println("On retire un mort");
							Buffer1[x][y].remove(ani);
						}
						/**/
					}
				}
				/**/
				Buffer0[x][y] = (AgentList) Buffer1[x][y].clone();

			}
		//System.out.println("buffer0 devient buffer1 !");

		//System.out.println("Nombre de naissances : " + births);
		//System.out.println("Nombre de décès : " + deaths);


	}


	public void swapBuffer() // should be used carefully (except for initial step)
	{
		/**/
		if (activeIndex == 0)
		{
			for ( int x = 0 ; x != _dx ; x++ )
				for ( int y = 0 ; y != _dy ; y++ )
				{
					Buffer0[x][y] = (AgentList) Buffer1[x][y].clone();
					Buffer1[x][y].clear();
				}

			//System.out.println("buffer0 devient buffer1 !");

		}
		else
		{
			for ( int x = 0 ; x != _dx ; x++ )
				for ( int y = 0 ; y != _dy ; y++ )
				{
					Buffer1[x][y] = (AgentList) Buffer0[x][y].clone();
					Buffer0[x][y].clear();

				}
			//System.out.println("buffer1 devient buffer0 !");


		}
		/**/
		//activeIndex = ( activeIndex+1 ) % 2;
		//System.out.println("Buffer courant : Buffer" + activeIndex);

	}

}
