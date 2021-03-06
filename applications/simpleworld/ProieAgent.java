package applications.simpleworld;


import interfaces.*;

import javax.media.opengl.GL2;

import objects.UniqueDynamicObject;

import worlds.World;

public class ProieAgent extends Animal implements Killable
{


	public static int nbIndividusProie = 0;
	
	public static final int RUT = 0;
	public static final int NORMAL = 1;
	public static final int FLIGHT = 2;
	public static final int HUNGRY = 3;
	private GrassAgent food;

	private Agent threat;



	//private boolean satiete; // La faim


	public ProieAgent ( int __x , int __y , World __world )
	{
		super(__x, __y, __world);
		//satiete = false;
		slowliness = 15;
		deathAge = 3000; // 
		sightRange = 20;
		threat = null;
		energy = 500;
		feedFromEnergyLevel = 200;
		ProieAgent.nbIndividusProie += 1;

		food = null;

		currState = ALIVE;
		behavior = NORMAL;
	}


	private void move()
	{

		/**
	int dx = (int)(Math.random() * 3) -1;
	int dy= (int)(Math.random() * 3) -1;

	int myAgents_lenX = this.world.getAgents().getCurrentBuffer().length;
	int myAgents_lenY = this.world.getAgents().getCurrentBuffer()[0].length;

	int newX = (this.x + dx + myAgents_lenX) % myAgents_lenX;
	int newY = (this.y + dy + myAgents_lenY) % myAgents_lenY;
	/**/
		int newX = this.x;
		int newY = this.y;
		if ( world.getIteration() % slowliness == 0 )
		{
			double dice = Math.random();
			if ( dice < 0.25 )
				newX = ( newX + 1 ) % this.world.getWidth() ;
			else
				if ( dice < 0.5 )
					newX = ( newX - 1 +  this.world.getWidth() ) % this.world.getWidth() ;
				else
					if ( dice < 0.75 )
						newY = ( newY + 1 ) % this.world.getHeight() ;
					else
						newY = ( newY - 1 +  this.world.getHeight() ) % this.world.getHeight() ;
		}

		/*if (this.world.getCellHeight(newX, newY) <= 0)
		{
			this.move();
			return;
		}/**/
		world.getAgents().updateAgent(this, newX, newY);

		/**/
	}


	public void searchForMate()
	{
		ProieAgent cible = (ProieAgent) searchForAgentOfType(ProieAgent.class);
		if (cible != this)
		{
			this.mate = cible;
		}
	}


	public void goToMate()
	{
		//System.out.println("---- goToMate ----");
		//System.out.println("Moi : [" + x + ", " + y + "]");

		//System.out.println("Cible : [" + mate.getCoordinate()[0] + ", " + mate.getCoordinate()[1] + "]");

		if (x == mate.getCoordinate()[0] && y == mate.getCoordinate()[1])
		{
			//System.out.println("Mating !!!!");
			mate((ProieAgent)mate);
			this.behavior = NORMAL;
			mate = null; // experimental

			return;
		}
		else
		{
			if (age % slowliness == 0)
			{



				int diffX = x - mate.getCoordinate()[0];
				int diffY = y - mate.getCoordinate()[1];
				//System.out.println("diffX = " + diffX);
				//System.out.println("diffY = " + diffY);

				int newX = this.x;
				int newY = this.y;

				int e = 1; // e : sign


				if (x != mate.getCoordinate()[0])
				{
					if (Math.abs(diffX) > world.getWidth()/2)
					{
						e = -1;

					}
					else
					{
						e = 1;
					}
					newX = ( newX - e * diffX/Math.abs(diffX) +  this.world.getWidth() ) % this.world.getWidth() ;

				}
				if (y != mate.getCoordinate()[1])
				{
					if (Math.abs(diffY) > world.getHeight()/2)
					{
						e = -1;
					}
					else
					{
						e = 1;
					}
					newY = ( newY - e * diffY/Math.abs(diffY) +  this.world.getHeight() ) % this.world.getHeight() ;
				}
				/*if (this.world.getCellHeight(newX, newY) <= 0)
				{
					this.move();
					return;
				}/**/
				world.getAgents().updateAgent(this, newX, newY);
			}
		}
	}

	public void mate(ProieAgent pa)
	{
		ProieAgent enfant = new ProieAgent(this.x, this.y, this.world);
		world.getAgents().updateAgentInitially(enfant, x, y);
		enfant.move();
		this.energy = energy/3;


	}

	public void setThreat(Agent threat)
	{
		this.threat = threat;
	}
	public Agent getThreat()
	{
		return this.threat;
	}

	public void onTheAlert()
	{


	}

	public void escapeFrom(Agent ag)
	{

		if (age % slowliness == 0)
		{



			int diffX = x - ag.getCoordinate()[0];
			int diffY = y - ag.getCoordinate()[1];
			//System.out.println("diffX = " + diffX);
			//System.out.println("diffY = " + diffY);

			int newX = this.x;
			int newY = this.y;

			int e = 1; // e : sign



			if (Math.abs(diffX) > world.getWidth()/2)
			{
				e = -1;

			}
			else
			{
				e = 1;
			}
			newX = ( newX + e * Integer.signum(diffX) +  this.world.getWidth() ) % this.world.getWidth() ;


			if (Math.abs(diffY) > world.getHeight()/2)
			{
				e = -1;
			}
			else
			{
				e = 1;
			}
			newY = ( newY + e * Integer.signum(diffY) +  this.world.getHeight() ) % this.world.getHeight() ;
			//System.out.println(this.toString() + " fuit " + ag.toString() );
			//System.out.println("Nouvelle position : " + newX + ", " + newY);
			/*
			if (this.world.getCellHeight(newX, newY) <= 0)
			{
				this.move();
				return;
			}
			/**/
			world.getAgents().updateAgent(this, newX, newY);
		}



	}


	/* public void step() */
	public void step()
	{

		switch (currState)
		{
		case ALIVE :
			if (world.getmyLava().isLava(x, y))
			{
				this.currState = BURNING;
				break;
			}

			if (health <= 0 || age >= deathAge || energy <= 0)
			{
				currState = Animal.DEAD;

				return;
			}

			if (threat != null)
			{
				this.behavior = FLIGHT;
			}


			switch (behavior)
			{
			case NORMAL :
				if (age >= birthAge && Math.random() < /**0.01/**/ /**/1./(nbIndividusProie * 2)/**/) // 0.0005 // 0.005
				{
					behavior = RUT;

				}
				/*else
				{*/
					//System.out.println(toString() + " : energy = " + energy);
					if (energy < feedFromEnergyLevel) 
					{
						behavior = HUNGRY;

					}
				/*}*/

				move();
				break;

			case FLIGHT :
				if (threat == null)
				{
					this.behavior = NORMAL;
				}
				else
				{
					escapeFrom(threat);
					threat = null;

				}
				break;
			case RUT :
				//System.out.println(toString() + " est en rut !");
				if (mate == null)
				{
					this.searchForMate();
					move();
				}
				else
				{
					//System.out.println(toString() + " va vers " + this.mate.toString());
					this.goToMate();
				}
				break;

			case HUNGRY:
				//System.out.println(toString() + " a faim !!"); 
				if(this.food!=null) {
					this.movetoFood();
					//System.out.println(toString() + " va manger " + food.toString());

					if(this.x==food.getCoordinate()[0] && this.y==food.getCoordinate()[1]) {

						this.eatGrass();
						food = null;
						this.behavior = NORMAL;

					}


				}
				else {
					this.searchForFood();
				}


				break;

			}

			break;

		case BURNING :
			if (health <= 0 || age >= deathAge)
			{
				currState = Animal.DEAD;
				return;
			}
			move();
			health--;
			if (Math.random() < 0.0025)
			{
				this.currState = ALIVE;
			}
			break;
			
		case DEAD :

			break;

		}
		this.age++;
		energy--;
		//world.getAgents().updateAgent(this, this.x, this.y);

	}
	@Override
	public String toString()
	{
		return "ProieAgent " +  ", " + "coord [" + x + ", " + y + "]";
	}






	public Agent searchForFood() {

		food=(GrassAgent)searchForAgentOfType(GrassAgent.class);
		return food;

	}

	public void movetoFood() {

		moveto(food.getCoordinate());

	}

	public void eatGrass() {
		this.energy+=food.energy;
		food.die();
	}







	public void displayUniqueObject(World myWorld, GL2 gl, int offsetCA_x, int offsetCA_y, float offset, float stepX, float stepY, float lenX, float lenY, float normalizeHeight)
	{

		// display a tree

		//gl.glColor3f(0.f+(float)(0.5*Math.random()),0.f+(float)(0.5*Math.random()),0.f+(float)(0.5*Math.random()));

		int x2 = (x-(offsetCA_x%myWorld.getWidth()));
		if ( x2 < 0) x2+=myWorld.getWidth();
		int y2 = (y-(offsetCA_y%myWorld.getHeight()));
		if ( y2 < 0) y2+=myWorld.getHeight();

		float height = Math.max ( 0 , (float)myWorld.getCellHeight(x, y) );

		float altitude = (float)height * normalizeHeight ; // test, a enlever apres
		//gl.glColor3f(1.f,1.f,0.f);


		switch ( currState )
		{
		case ALIVE:

			if (health < healthMax)
			{
			/* BARRE DE VIE  */
			gl.glColor3f(1.f * ((healthMax - health)/(healthMax * 1.f)),1.f * ((health)/(healthMax * 1.f)) - 1.f * ((healthMax - health)/(healthMax * 0.5f)),0.f);
			/*Cote blanc*/
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 5.f);
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight+ 5.f);
			/*Cote blanc*/
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight+ 5.f);
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight+ 5.f);
			/*Cote blanc*/
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight+ 5.f);
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight+ 5.f);
			/*Cote blanc*/
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight+ 5.f);
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight+ 5.f);
			/*Chapeau rouge*/
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));

			}// Health bar


			gl.glColor3f(1.f,1.f,1.f);
			/*Cote blanc*/
			gl.glColor3f(1.f,1.f,1.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			/*Cote blanc*/
			gl.glColor3f(1.f,1.f,1.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			/*Cote blanc*/
			gl.glColor3f(1.f,1.f,1.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			/*Cote blanc*/
			gl.glColor3f(1.f,1.f,1.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			/*Chapeau bleu*/
			gl.glColor3f(0.2f,0.2f,1.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);



			break;
		case DEAD:
			/*
			gl.glColor3f(0.f,0.f,0.f);
			/*Cote blanc*
			gl.glColor3f(0.f,0.f,0.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			/*Cote blanc*
			gl.glColor3f(0.f,0.f,0.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			/*Cote blanc*
			gl.glColor3f(0.f,0.f,0.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			/*Cote blanc*
			gl.glColor3f(0.f,0.f,0.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			/*Chapeau bleu*
			gl.glColor3f(0.2f,0.2f,1.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			/**/
			break;
			
		case BURNING :
			
			/* FLAMMES */
			
			gl.glColor3f(1.f-(float)(0.2*Math.random()), 0.4f-(float)(0.4*Math.random()), 0.f);
			gl.glVertex3f( offset+x2*stepX-lenY/4.f, offset+y2*stepY+lenY/3.f, altitude );//2
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY, altitude + 12.f * (float)Math.random());//1

			gl.glVertex3f( offset+x2*stepX+lenY/4.f, offset+y2*stepY-lenY/3.f, altitude );//4
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY, altitude+ 12.f * (float)Math.random() );//3


			gl.glVertex3f( offset+x2*stepX-lenY/3.f, offset+y2*stepY+lenY/4.f, altitude  );//6
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY, altitude+ 12.f * (float)Math.random());//5

			gl.glVertex3f( offset+x2*stepX+lenY/3.f, offset+y2*stepY-lenY/4.f, altitude  );//8
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY, altitude+ 12.f * (float)Math.random());//7



			gl.glVertex3f( offset+x2*stepX-lenY/4.f - 2, offset+y2*stepY+lenY/3.f, altitude  );//2
			gl.glVertex3f( offset+x2*stepX - 2, offset+y2*stepY, altitude+ 12.f * (float)Math.random());//1

			gl.glVertex3f( offset+x2*stepX+lenY/4.f - 2, offset+y2*stepY-lenY/3.f, altitude  );//4
			gl.glVertex3f( offset+x2*stepX - 2, offset+y2*stepY, altitude+ 4.f );//3


			gl.glVertex3f( offset+x2*stepX-lenY/3.f - 2, offset+y2*stepY+lenY/4.f, altitude  );//6
			gl.glVertex3f( offset+x2*stepX - 2, offset+y2*stepY, altitude+ 12.f * (float)Math.random());//5

			gl.glVertex3f( offset+x2*stepX+lenY/3.f- 2, offset+y2*stepY-lenY/4.f, altitude  );//8
			gl.glVertex3f( offset+x2*stepX- 2, offset+y2*stepY, altitude+ 12.f * (float)Math.random());//7




			gl.glVertex3f( offset+x2*stepX-lenY/4.f+ 2, offset+y2*stepY+lenY/3.f, altitude  );//2
			gl.glVertex3f( offset+x2*stepX+ 2, offset+y2*stepY, altitude+ 12.f * (float)Math.random());//1

			gl.glVertex3f( offset+x2*stepX+lenY/4.f+ 2, offset+y2*stepY-lenY/3.f, altitude  );//4
			gl.glVertex3f( offset+x2*stepX+ 2, offset+y2*stepY, altitude+ 12.f * (float)Math.random());//3


			gl.glVertex3f( offset+x2*stepX-lenY/3.f+ 2, offset+y2*stepY+lenY/4.f, altitude  );//6
			gl.glVertex3f( offset+x2*stepX+ 2, offset+y2*stepY, altitude+ 12.f * (float)Math.random());//5

			gl.glVertex3f( offset+x2*stepX+lenY/3.f+ 2, offset+y2*stepY-lenY/4.f, altitude  );//8
			gl.glVertex3f( offset+x2*stepX+ 2, offset+y2*stepY, altitude+ 12.f * (float)Math.random());//7



			gl.glVertex3f( offset+x2*stepX-lenY/4.f, offset+y2*stepY+lenY/3.f-2, altitude  );//2
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY-2, altitude+ 12.f * (float)Math.random());//1

			gl.glVertex3f( offset+x2*stepX+lenY/4.f, offset+y2*stepY-lenY/3.f-2, altitude  );//4
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY-2, altitude+ 12.f * (float)Math.random());//3


			gl.glVertex3f( offset+x2*stepX-lenY/3.f, offset+y2*stepY+lenY/4.f-2, altitude  );//6
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY-2, altitude+ 12.f * (float)Math.random());//5

			gl.glVertex3f( offset+x2*stepX+lenY/3.f, offset+y2*stepY-lenY/4.f-2, altitude );//8
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY-2, altitude+ 12.f * (float)Math.random() );//7



			gl.glVertex3f( offset+x2*stepX-lenY/4.f, offset+y2*stepY+lenY/3.f+2, altitude  );//2
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY+2, altitude+ 12.f * (float)Math.random());//1

			gl.glVertex3f( offset+x2*stepX+lenY/4.f, offset+y2*stepY-lenY/3.f+2, altitude );//4
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY+2, altitude + 12.f * (float)Math.random());//3


			gl.glVertex3f( offset+x2*stepX-lenY/3.f, offset+y2*stepY+lenY/4.f+2, altitude  );//6
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY+2, altitude+ 12.f * (float)Math.random());//5

			gl.glVertex3f( offset+x2*stepX+lenY/3.f, offset+y2*stepY-lenY/4.f+2, altitude  );//8
			gl.glVertex3f( offset+x2*stepX, offset+y2*stepY+2, altitude+ 12.f * (float)Math.random());//7

			
			/* FIN FLAMMES */

			
			/* BARRE DE VIE  */
			gl.glColor3f(1.f * ((healthMax - health)/(healthMax * 1.f)),1.f * ((health)/(healthMax * 1.f)) - 1.f * ((healthMax - health)/(healthMax * 0.5f)),0.f);
			/*Cote blanc*/
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 5.f);
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight+ 5.f);
			/*Cote blanc*/
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight+ 5.f);
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight+ 5.f);
			/*Cote blanc*/
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight+ 5.f);
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight+ 5.f);
			/*Cote blanc*/
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight+ 5.f);
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight+ 5.f);
			/*Chapeau rouge*/
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX-lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY+lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));
			gl.glVertex3f( offset+x2*stepX+lenX/4.f, offset+y2*stepY-lenY/4.f, height*normalizeHeight + 4.f+ (5.f * health/(healthMax * 1.f)));

			
			gl.glColor3f(1.f,1.f,0.f);
			/*Cote blanc*/
			gl.glColor3f(1.f,0.f,0.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			/*Cote blanc*/
			gl.glColor3f(1.f,0.f,0.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			/*Cote blanc*/
			gl.glColor3f(1.f,0.f,0.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			/*Cote blanc*/
			gl.glColor3f(1.f,0.f,0.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight);
			/*Chapeau jaune*/
			gl.glColor3f(0.2f,0.2f,1.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX-lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY+lenY, height*normalizeHeight + 4.f);
			gl.glVertex3f( offset+x2*stepX+lenX, offset+y2*stepY-lenY, height*normalizeHeight + 4.f);

			break;

		}


	}
	/**/
}
