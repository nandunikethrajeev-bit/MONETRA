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

export interface AnimatedBackgroundProps {
  variant?: 'cinematic' | 'auth' | 'dashboard';
  showMountains?: boolean;
  showGlow?: boolean;
  glowPosition?: 'center' | 'right' | 'top';
}

export const AnimatedBackground: React.FC<AnimatedBackgroundProps> = ({
  variant = 'cinematic',
  showMountains = true,
  showGlow = true,
  glowPosition,
}) => {
  const [isPageVisible, setIsPageVisible] = useState<boolean>(true);
  const [particles, setParticles] = useState<Particle[]>([]);

  const isDashboard = variant === 'dashboard';
  const isAuth = variant === 'auth';

  // Determine effective glow position
  const effectiveGlowPos = glowPosition || (isDashboard ? 'center' : isAuth ? 'right' : 'right');

  useEffect(() => {
    // Generate well-distributed floating emerald particles
    const count = isDashboard ? 24 : isAuth ? 16 : 28;
    const items: Particle[] = Array.from({ length: count }).map((_, i) => ({
      id: i,
      x: Math.random() * 96 + 2,
      y: Math.random() * 85 + 10,
      size: Math.random() * 2 + 1.5, // 1.5px to 3.5px
      opacity: Math.random() * 0.45 + 0.3, // 0.30 to 0.75
      duration: Math.random() * 10 + 16, // 16s to 26s
      delay: Math.random() * 8, // 0s to 8s
    }));
    setParticles(items);

    const handleVisibilityChange = () => {
      setIsPageVisible(!document.hidden);
    };

    document.addEventListener('visibilitychange', handleVisibilityChange);
    return () => document.removeEventListener('visibilitychange', handleVisibilityChange);
  }, [isDashboard, isAuth]);

  return (
    <div className="fixed inset-0 pointer-events-none overflow-hidden select-none z-0" aria-hidden="true">
      {/* Deep Near-Black Base Background with Subtle Radial Depth */}
      <div className="absolute inset-0 bg-[#0B0E11]" />

      {/* Subtle Sky Horizon Vignette Gradient */}
      <div className="absolute inset-0 bg-gradient-to-b from-[#080C0A] via-[#0B0E11] to-[#0A120E] opacity-90" />

      {/* Ambient Emerald Lighting Glow */}
      {showGlow && (
        <>
          {effectiveGlowPos === 'right' ? (
            <div
              className={`absolute right-[-10%] top-[20%] w-[700px] h-[700px] rounded-full bg-[#00C853]/15 blur-[150px] pointer-events-none ${
                isPageVisible ? 'animate-ambient-glow-right' : 'opacity-10'
              }`}
            />
          ) : effectiveGlowPos === 'top' ? (
            <div
              className={`absolute left-1/2 -top-32 -translate-x-1/2 w-[800px] h-[500px] rounded-full bg-[#00C853]/12 blur-[140px] pointer-events-none ${
                isPageVisible ? 'animate-ambient-glow' : 'opacity-10'
              }`}
            />
          ) : (
            <div
              className={`absolute left-1/2 top-[38%] -translate-x-1/2 -translate-y-1/2 w-[850px] h-[650px] rounded-full bg-[#00C853]/10 blur-[160px] pointer-events-none ${
                isPageVisible ? 'animate-ambient-glow' : 'opacity-10'
              }`}
            />
          )}

          {/* Secondary Soft Horizon Glow behind mountain peaks */}
          <div className="absolute bottom-[28%] left-1/2 -translate-x-1/2 w-[110%] h-[320px] bg-gradient-to-t from-[#00C853]/12 via-[#10B981]/5 to-transparent blur-[80px] pointer-events-none" />
        </>
      )}

      {/* Multi-layered Atmospheric Mountain Ridges */}
      {showMountains && (
        <div
          className={`absolute bottom-0 left-0 right-0 pointer-events-none overflow-hidden ${
            isDashboard
              ? 'h-[55vh] min-h-[380px] max-h-[640px]'
              : 'h-[52vh] min-h-[380px] max-h-[600px]'
          }`}
        >
          {/* Layer 1: Distant Misty Sky Glow behind Far Peaks */}
          <div
            className={`absolute bottom-[35%] left-[-15%] right-[-15%] h-[240px] bg-gradient-to-t from-[#00C853]/14 via-[#1b382b]/8 to-transparent blur-[60px] ${
              isPageVisible ? 'animate-mist-1' : ''
            }`}
          />

          {/* SVG Vector Mountain Ridges with Defined Peaks & Facets */}
          <svg
            className="absolute bottom-0 left-0 right-0 w-full h-full"
            viewBox="0 0 1440 600"
            preserveAspectRatio="none"
          >
            <defs>
              {/* Far Mountain Gradient */}
              <linearGradient id="farPeakGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" stopColor="#1E362A" stopOpacity="0.85" />
                <stop offset="45%" stopColor="#15261E" stopOpacity="0.9" />
                <stop offset="100%" stopColor="#0B130E" stopOpacity="0.98" />
              </linearGradient>

              {/* Mid Mountain Facet Light Gradient */}
              <linearGradient id="midFacetLight" x1="30%" y1="0%" x2="70%" y2="100%">
                <stop offset="0%" stopColor="#2A4839" stopOpacity="0.9" />
                <stop offset="50%" stopColor="#1C3227" stopOpacity="0.95" />
                <stop offset="100%" stopColor="#0E1A14" stopOpacity="0.98" />
              </linearGradient>

              {/* Mid Mountain Facet Shadow Gradient */}
              <linearGradient id="midFacetShadow" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" stopColor="#16271F" stopOpacity="0.95" />
                <stop offset="100%" stopColor="#0A110D" stopOpacity="1" />
              </linearGradient>

              {/* Foreground Ridge Gradient */}
              <linearGradient id="foreRidgeGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" stopColor="#132019" stopOpacity="0.98" />
                <stop offset="60%" stopColor="#0C1410" stopOpacity="1" />
                <stop offset="100%" stopColor="#080C0A" stopOpacity="1" />
              </linearGradient>

              {/* Valley Mist Horizontal Gradient */}
              <linearGradient id="valleyMistGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" stopColor="#00C853" stopOpacity="0" />
                <stop offset="25%" stopColor="#00C853" stopOpacity="0.12" />
                <stop offset="50%" stopColor="#10B981" stopOpacity="0.2" />
                <stop offset="75%" stopColor="#00C853" stopOpacity="0.12" />
                <stop offset="100%" stopColor="#00C853" stopOpacity="0" />
              </linearGradient>
            </defs>

            {/* FAR DISTANT MOUNTAIN SILHOUETTE (Deep Background) */}
            <g className={isPageVisible ? 'animate-gentle-sway' : ''}>
              <path
                d="M0,320 L95,260 L210,310 L340,220 L460,285 L590,180 L710,270 L830,195 L970,290 L1110,210 L1230,275 L1340,225 L1440,290 L1440,600 L0,600 Z"
                fill="url(#farPeakGrad)"
              />
              {/* Subtle Rim Highlight along Far Peaks */}
              <path
                d="M0,320 L95,260 L210,310 L340,220 L460,285 L590,180 L710,270 L830,195 L970,290 L1110,210 L1230,275 L1340,225 L1440,290"
                fill="none"
                stroke="#00C853"
                strokeOpacity="0.22"
                strokeWidth="1.2"
              />
            </g>

            {/* MID-LAYER MOUNTAIN RANGE (Faceted Peaks with Light & Shadow Faces) */}
            <g>
              {/* Left Ridge Peak */}
              <polygon
                points="-30,600 140,290 280,420 380,330 490,470 510,600 -30,600"
                fill="url(#midFacetLight)"
              />
              <polygon
                points="140,290 200,370 280,420 140,290"
                fill="url(#midFacetShadow)"
              />

              {/* Central Majestic Alpine Peak */}
              <polygon
                points="360,600 520,440 680,240 820,380 940,300 1080,480 1120,600 360,600"
                fill="url(#midFacetLight)"
              />
              {/* Central Peak Shadow Side */}
              <polygon
                points="680,240 760,330 820,380 940,300 1010,410 1080,480 680,240"
                fill="url(#midFacetShadow)"
              />

              {/* Right Ridge Peak */}
              <polygon
                points="960,600 1100,370 1250,280 1370,360 1480,310 1480,600 960,600"
                fill="url(#midFacetLight)"
              />
              <polygon
                points="1250,280 1320,330 1370,360 1250,280"
                fill="url(#midFacetShadow)"
              />

              {/* Illuminated Crest Rim Lines & Facet Edges */}
              <path
                d="M140,290 L280,420 L380,330 M680,240 L820,380 L940,300 M1250,280 L1370,360"
                fill="none"
                stroke="#10B981"
                strokeOpacity="0.38"
                strokeWidth="1.5"
              />
              {/* Geometric Facet Wireframe Accents (Low-Poly Mountain Styling) */}
              <path
                d="M0,480 L140,290 L360,420 L520,310 L680,240 L860,350 L1040,280 L1250,280 L1440,380"
                fill="none"
                stroke="#00E676"
                strokeOpacity="0.25"
                strokeWidth="1.2"
              />
              <path
                d="M520,310 L680,240 L820,380 L940,300 L1100,370"
                fill="none"
                stroke="#4EFA94"
                strokeOpacity="0.3"
                strokeWidth="1.2"
              />
              {/* Luminous Alpine Peak Star Point */}
              <circle cx="520" cy="310" r="3" fill="#4EFA94" opacity="0.8" />
              <circle cx="520" cy="310" r="8" fill="#00C853" opacity="0.3" />
            </g>

            {/* MID-VALLEY DRIFTING MIST OVERLAY */}
            <rect
              x="-100"
              y="320"
              width="1640"
              height="160"
              fill="url(#valleyMistGrad)"
              className={isPageVisible ? 'animate-mist-1' : ''}
              style={{ filter: 'blur(30px)' }}
            />

            {/* LOWER ROLLING FOREGROUND RIDGE */}
            <path
              d="M0,450 Q180,390 360,430 T720,410 T1080,440 T1440,420 L1440,600 L0,600 Z"
              fill="url(#foreRidgeGrad)"
            />
            {/* Subtle Edge Line on Foreground Ridge */}
            <path
              d="M0,450 Q180,390 360,430 T720,410 T1080,440 T1440,420"
              fill="none"
              stroke="#00C853"
              strokeOpacity="0.16"
              strokeWidth="1.2"
            />
          </svg>

          {/* Layer 2: Drifting Low-Altitude Valley Mist */}
          <div
            className={`absolute bottom-[8%] left-[-20%] right-[-20%] h-[160px] bg-gradient-to-r from-transparent via-[#00C853]/16 to-transparent blur-[45px] ${
              isPageVisible ? 'animate-mist-2' : ''
            }`}
          />

          {/* Bottom Ground Blend Gradient to Pure Base */}
          <div className="absolute bottom-0 left-0 right-0 h-28 bg-gradient-to-t from-[#0B0E11] via-[#0B0E11]/80 to-transparent" />
        </div>
      )}

      {/* Floating Emerald Particles / Atmospheric Embers */}
      {isPageVisible && (
        <div className="absolute inset-0 pointer-events-none">
          {particles.map((p) => (
            <div
              key={p.id}
              className="absolute rounded-full bg-[#00C853]"
              style={{
                left: `${p.x}%`,
                top: `${p.y}%`,
                width: `${p.size}px`,
                height: `${p.size}px`,
                boxShadow: '0 0 6px rgba(0, 200, 83, 0.65)',
                animation: `particleFloat ${p.duration}s ease-in-out infinite`,
                animationDelay: `${p.delay}s`,
                ['--particle-opacity' as any]: p.opacity,
              }}
            />
          ))}
        </div>
      )}
    </div>
  );
};
