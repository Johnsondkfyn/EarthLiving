export type RoadmapStatus = "Done" | "In Progress" | "Planned" | "Future";

export const roadmapItems: Array<{
  version: string;
  title: string;
  status: RoadmapStatus;
  description: string;
}> = [
  {
    version: "0.1",
    title: "Website Foundation",
    status: "Done",
    description: "Project identity, Next.js foundation, visual direction and the first public website sections.",
  },
  {
    version: "0.2",
    title: "Fabric Infrastructure",
    status: "In Progress",
    description: "Fabric test server planning, Java 21 target, modpack foundation and safe migration preparation.",
  },
  {
    version: "0.3",
    title: "Playable Prototype",
    status: "Planned",
    description: "First playable test loop with the Earth map copy, base systems and early gameplay validation.",
  },
  {
    version: "0.4",
    title: "Economy & Jobs",
    status: "Planned",
    description: "Player jobs, early market logic, income loops and the first business foundation.",
  },
  {
    version: "0.5",
    title: "Closed Beta",
    status: "Future",
    description: "Invite-only testing with selected players, feedback tracking and stability checks.",
  },
  {
    version: "0.6",
    title: "Cities",
    status: "Future",
    description: "City identity, districts, infrastructure, services and controlled expansion.",
  },
  {
    version: "0.7",
    title: "Businesses",
    status: "Future",
    description: "Player companies, production chains, shops and deeper economic interaction.",
  },
  {
    version: "0.8",
    title: "Tourism",
    status: "Future",
    description: "Landmarks, travel reasons, events and visitor activity across the Earth map.",
  },
  {
    version: "1.0",
    title: "Public Beta",
    status: "Future",
    description: "Public beta opening after test server stability, map integrity and gameplay foundations are proven.",
  },
];
