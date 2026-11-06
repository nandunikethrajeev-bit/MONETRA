import React, { useEffect, useState } from 'react';

interface Particle {
  id: number;
  x: number;
  y: number;
  size: number;
  opacity: number;
  duration: number;
  delay: number;
}

export const AnimatedBackground: React.FC<{ showMountains?: boolean; showGlow?: boolean; glowPosition?: 'center' | 'right' }> = ({
  showMountains = true,
  showGlow = true,
  glowPosition = 'right',
}) => {
  const [isPageVisible, setIsPageVisible] = useState<boolean>(true);
  const [particles, setParticles] = useState<Particle[]>([]);

  useEffect(() => {
    // Generate low-density subtle emerald particles
    const items: Particle[] = Array.from({ length: 18 }).map((_, i) => ({
      id: i,
      x: Math.random() * 96 + 2, // 2% to 98%
      y: Math.random() * 90 + 5, // 5% to 95%
      size: Math.random() * 2.5 + 1.2, // 1.2px to 3.7px
      opacity: Math.random() * 0.45 + 0.2, // 0.2 to 0.65
      duration: Math.random() * 12 + 16, // 16s to 28s
      delay: Math.random() * 8, // 0s to 8s
    }));
    setParticles(items);

    // Pause animations on tab hidden
    const handleVisibilityChange = () => {
      setIsPageVisible(!document.hidden);
    };

    document.addEventListener('visibilitychange', handleVisibilityChange);
    return () => document.removeEventListener('visibilitychange', handleVisibilityChange);
  }, []);

  return (
    <div className="fixed inset-0 pointer-events-none overflow-hidden select-none z-0" aria-hidden="true">
      {/* Deep Near-Black Base Background */}
      <div className="absolute inset-0 bg-[#0B0E11]" />

      {/* Ambient Emerald Lighting Glow */}
      {showGlow && (
        <div
          className={`absolute rounded-full blur-[140px] pointer-events-none transition-opacity duration-1000 ${
            glowPosition === 'right'
              ? 'right-[-10%] top-[25%] w-[650px] h-[650px] bg-[#00C853]/15'
              : 'left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 w-[750px] h-[750px] bg-[#00C853]/12'
          } ${isPageVisible ? 'animate-ambient-glow' : 'opacity-10'}`}
        />
      )}

      {/* Floating Emerald Particles */}
      {isPageVisible && (
        <div className="absolute inset-0">
          {particles.map((p) => (
            <div
              key={p.id}
              className="absolute rounded-full bg-[#00C853]"
              style={{
                left: `${p.x}%`,
                top: `${p.y}%`,
                width: `${p.size}px`,
                height: `${p.size}px`,
                opacity: p.opacity,
                filter: 'blur(0.5px)',
                animation: `particleFloat ${p.duration}s ease-in-out infinite`,
                animationDelay: `${p.delay}s`,
              }}
            />
          ))}
        </div>
      )}

      {/* Multi-layered Atmospheric Mountains Parallax Silhouettes */}
      {showMountains && (
        <div className="absolute bottom-0 left-0 right-0 h-[45vh] min-h-[300px] max-h-[550px] pointer-events-none overflow-hidden">
          {/* Subtle Emerald Valley Mist Back */}
          <div
            className={`absolute bottom-[18%] left-[-10%] right-[-10%] h-[180px] bg-gradient-to-t from-[#00C853]/10 via-[#10B981]/5 to-transparent blur-[50px] ${
              isPageVisible ? 'animate-mist-1' : ''
            }`}
          />

          {/* Deep Far Ridge Layer (Back) */}
          <svg
            className={`absolute bottom-0 left-0 right-0 w-[110%] -ml-[5%] h-[90%] text-[#0F1813] fill-current opacity-70 ${
              isPageVisible ? 'animate-gentle-sway' : ''
            }`}
            viewBox="0 0 1440 320"
            preserveAspectRatio="none"
          >
            <path d="M0,192L60,181.3C120,171,240,149,360,165.3C480,181,600,235,720,224C840,213,960,139,1080,128C1200,117,1320,171,1380,197.3L1440,224L1440,320L1380,320C1320,320,1200,320,1080,320C960,320,840,320,720,320C600,320,480,320,360,320C240,320,120,320,60,320L0,320Z" />
          </svg>

          {/* Drifting Mist Mid Layer */}
          <div
            className={`absolute bottom-[10%] left-[-15%] right-[-15%] h-[120px] bg-gradient-to-r from-transparent via-[#00C853]/8 to-transparent blur-[40px] ${
              isPageVisible ? 'animate-mist-2' : ''
            }`}
          />

          {/* Mid Ridge Layer */}
          <svg
            className="absolute bottom-0 left-0 right-0 w-[105%] -ml-[2.5%] h-[70%] text-[#101915] fill-current opacity-85"
            viewBox="0 0 1440 260"
            preserveAspectRatio="none"
          >
            <path d="M0,120L48,138.7C96,157,192,195,288,186.7C384,179,480,125,576,122.7C672,120,768,168,864,186.7C960,205,1056,195,1152,176C1248,157,1344,131,1392,117.3L1440,104L1440,260L1392,260C1344,260,1248,260,1152,260C1056,260,960,260,864,260C768,260,672,260,576,260C480,260,384,260,288,260C192,260,96,260,48,260L0,260Z" />
          </svg>

          {/* Foreground Silhouette Ridge (Front) */}
          <svg
            className="absolute bottom-0 left-0 right-0 w-full h-[45%] text-[#090E0C] fill-current"
            viewBox="0 0 1440 180"
            preserveAspectRatio="none"
          >
            <path d="M0,64L80,80C160,96,320,128,480,122.7C640,117,800,75,960,69.3C1120,64,1280,96,1360,112L1440,128L1440,180L1360,180C1280,180,1120,180,960,180C800,180,640,180,480,180C320,180,160,180,80,180L0,180Z" />
          </svg>

          {/* Ground Fade to Base Color */}
          <div className="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-[#0B0E11] to-transparent" />
        </div>
      )}
    </div>
  );
};
